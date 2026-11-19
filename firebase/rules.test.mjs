import {before, after, beforeEach, test} from 'node:test';
import {readFileSync} from 'node:fs';
import {initializeTestEnvironment, assertSucceeds, assertFails} from '@firebase/rules-unit-testing';
import {doc, getDoc, setDoc, updateDoc, deleteDoc, serverTimestamp} from 'firebase/firestore';
let env;
before(async () => { env=await initializeTestEnvironment({projectId:'demo-leaguehub',firestore:{host:'127.0.0.1',port:8080,rules:readFileSync('firestore.rules','utf8')}}); });
after(async () => { await env?.cleanup(); });
beforeEach(async () => { await env.clearFirestore(); });
const league=(uid,revision=1)=>({ownerUid:uid,ownerLocalId:uid,revision,payload:'{"version":1}',deleted:false,updatedAt:serverTimestamp()});
const profile=uid=>({canonicalId:uid,name:'Usuario de prueba',role:'ORGANIZER',selectedLeagueId:null,onboardingCompleted:true,updatedAt:serverTimestamp()});
test('a signed-out client cannot read or publish leagues',async()=>{
 const db=env.unauthenticatedContext().firestore();
 await assertFails(getDoc(doc(db,'leagues','l')));await assertFails(setDoc(doc(db,'leagues','l'),league('a')));
});
test('registered readers can see sports data but only the owner may change it',async()=>{
 const a=env.authenticatedContext('a').firestore(),b=env.authenticatedContext('b').firestore();
 await assertSucceeds(setDoc(doc(a,'leagues','l'),league('a')));
 await assertSucceeds(getDoc(doc(b,'leagues','l')));
 await assertFails(setDoc(doc(b,'leagues','l'),league('b',2)));
 await assertSucceeds(setDoc(doc(a,'leagues','l'),league('a',2)));
});
test('a stale version, changed owner, or physical delete cannot erase a league',async()=>{
 const db=env.authenticatedContext('a').firestore(),ref=doc(db,'leagues','l');
 await setDoc(ref,league('a'));
 await assertFails(setDoc(ref,league('a',1)));await assertFails(setDoc(ref,league('a',3)));
 await assertFails(updateDoc(ref,{ownerUid:'b',revision:2,updatedAt:serverTimestamp()}));
 await assertFails(deleteDoc(ref));
 await assertSucceeds(setDoc(ref,{...league('a',2),deleted:true,payload:''}));
});
test('private profile and canonical identity are protected',async()=>{
 const a=env.authenticatedContext('a').firestore(),b=env.authenticatedContext('b').firestore();
 await assertSucceeds(setDoc(doc(a,'users','a'),profile('a')));
 await assertFails(getDoc(doc(b,'users','a')));
 await assertFails(setDoc(doc(b,'users','a'),profile('b')));
 await assertFails(setDoc(doc(b,'users','b'),profile('a')));
 await assertFails(setDoc(doc(a,'users','a'),{...profile('a'),passwordHash:'forbidden'}));
});
test('unknown fields and oversized league payloads are rejected',async()=>{
 const db=env.authenticatedContext('a').firestore();
 await assertFails(setDoc(doc(db,'leagues','l'),{...league('a'),password:'never-upload'}));
 await assertFails(setDoc(doc(db,'leagues','l'),{...league('a'),payload:'x'.repeat(750001)}));
});
