'use strict';

module.exports = class NotFoundController extends MatchController {
    notFound(session, args) {
        return session.send('Sorry, I did not understand \'%s\'.', session.message.text);
    }
};

/*
.matches('<yourIntent>')... See details at http://docs.botframework.com/builder/node/guides/understanding-natural-language/

.matches('None', (session, args) => {
    session.send('Sorry, I did not understand \'%s\'.', session.message.text);
})
.matches('greeting', (session, args) => {
    session.send('\%s\! Despre ce tip de card doriti sa aflati informatii? Card de cumparaturi sau de debit?', session.message.text);
    session.send('Daca doriti sa va aflati codul IBAN, tastati iban urmat de CNP-ul dvs.');
    session.send('Daca doriti sa va aflati sold-ul curent, tastati sold urmat de CNP-ul dvs.');
})
.matches('cumparaturi', (session, args) => {
    session.send('Pentru cardul de cumparaturi, aveti 3 optiuni: BT Flying Blue, STAR Card, STAR Forte pentru Medici. Despre care dintre acestea doriti sa aflati informatii?');
})
.matches('debit', (session, args) => {
    session.send('Pentru cardul de debit avem 4 optiuni: Visa Electron, MasterCard Mondo, MasterCard Gold Debit, MasterCard Direct. Dspre care dintre acestea doriti sa aflati informatii?');
})
.matches('BT Flying Blue', (session, args) => {
    session.send('Daca iti place sa calatoresti, atunci ai gasit ceea ce cauti. Cardul BT Flying Blue a fost creat pentru ca tu sa te bucuri de calatorii si de cumparaturi.');
})
.matches('STAR card', (session, args) => {
    session.send('Create pentru shopping, cardurile de credit STAR Card pot fi folosite pentru tranzactii la comerciantii din tara si din strainatate.');
})
.matches('STAR forte pentru medici', (session, args) => {
    session.send('Card dedicat in exclusivitate medicilor din Romania.');
})
.matches('VISA Electron', (session, args) => {
    session.send('Cardul tau de zi cu zi, Visa Electron de la BT te ajuta sa faci plati online si offline, atat in tara, cat si in strainatate. ');
})
.matches('MasterCard Mondo', (session, args) => {
    session.send('Cardul de debit MasterCard Mondo se poate folosi la toate POS-urile si bancomatele din tara si din strainatate, precum si pentru cumparaturi online.');
})
.matches('MasterCard Gold Debit', (session, args) => {
    session.send('Un card pe care il poti lua cu tine in concediu, MasterCard Gold Debit. Poti face cu el plati la POS-urile si bancomantele din Romania si din strainatate, dar si in online.');
})
.matches('MasterCard Direct', (session, args) => {
    session.send('Pentru ca iti place sa calatoresti ti-am pregatit MasterCard Direct, un card in euro.');
})
.matches('multumesc', (session, args) => {
    session.send('Cu mare placere!');
})
.matches('pa-pa', (session, args) => {
    session.send('La revedere! Va multumim ca ati ales BT!');
})
.matches('IBAN', (session, args) =>{
 let person = session.message.text;




 return numar.getIban(person, (err, result) =>{
  if (err) {
   return session.send(`Nu exista persoana cu acest CNP`);
  }
  return session.send(`IBAN-ul dvs. este ${result}`);
 });
})

.matches('SOLD', (session, args) =>{
 let cnp = session.message.text;




 return bani.getSold(cnp, (err, suma) =>{
  if (err) {
   return session.send(`Nu exista persoana cu acest CNP.`);
  }
  return session.send(`Sold-ul dvs. este ${suma}`);
 });
})

.onDefault((session) => {
    session.send('Sorry, I did not understand \'%s\'.', session.message.text);
});
*/