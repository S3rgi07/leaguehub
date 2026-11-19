package com.leaguehub.app.data.local

import androidx.sqlite.db.SupportSQLiteDatabase

/** SQLite triggers make the outbox durable in the SAME transaction as all existing writes. */
object SyncSchema {
 fun install(db:SupportSQLiteDatabase) {
  db.execSQL("INSERT OR IGNORE INTO sync_control(id,suppressed) VALUES(1,0)")
  db.execSQL("INSERT OR IGNORE INTO sync_leagues(leagueId,ownerLocalId,revision,localVersion,syncedVersion,deleted,conflictRevision) SELECT id,ownerId,0,1,0,0,0 FROM leagues")
  fun trigger(name:String,table:String,operation:String,league:String,owner:String,deleted:Int=0,extra:String="") {
   db.execSQL("""CREATE TRIGGER IF NOT EXISTS $name AFTER $operation ON $table
    WHEN (SELECT suppressed FROM sync_control WHERE id=1)=0 $extra BEGIN
    INSERT OR IGNORE INTO sync_leagues(leagueId,ownerLocalId,revision,localVersion,syncedVersion,deleted,conflictRevision)
    SELECT $league,$owner,0,0,0,$deleted,0 WHERE $league IS NOT NULL;
    UPDATE sync_leagues SET localVersion=localVersion+1,deleted=$deleted,lastError=NULL WHERE leagueId=$league;
    END""")
  }
  listOf("INSERT","UPDATE","DELETE").forEach { op ->
   val n=if(op=="DELETE") "OLD" else "NEW";val suffix=op.lowercase()
   trigger("cloud_league_$suffix","leagues",op,"$n.id","$n.ownerId",if(op=="DELETE") 1 else 0)
   val clubLeague="$n.leagueId"
   trigger("cloud_club_$suffix","clubs",op,clubLeague,"(SELECT ownerId FROM leagues WHERE id=$clubLeague)")
   val playerLeague="(SELECT leagueId FROM clubs WHERE id=$n.clubId)"
   trigger("cloud_player_$suffix","players",op,playerLeague,"(SELECT ownerId FROM leagues WHERE id=$playerLeague)")
   trigger("cloud_fixture_$suffix","fixtures",op,"$n.leagueId","(SELECT ownerId FROM leagues WHERE id=$n.leagueId)")
   val eventLeague="(SELECT leagueId FROM fixtures WHERE id=$n.fixtureId)"
   trigger("cloud_event_$suffix","match_events",op,eventLeague,"(SELECT ownerId FROM leagues WHERE id=$eventLeague)")
   val sheetLeague="(SELECT leagueId FROM fixtures WHERE id=substr($n.dataKey,13))"
   trigger("cloud_sheet_$suffix","user_data",op,sheetLeague,"(SELECT ownerId FROM leagues WHERE id=$sheetLeague)",extra="AND $n.dataKey LIKE 'match-sheet:%'")
  }
 }
}
