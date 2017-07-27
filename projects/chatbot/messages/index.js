/*-----------------------------------------------------------------------------
This template demonstrates how to use an IntentDialog with a LuisRecognizer to add 
natural language support to a bot. 
For a complete walkthrough of creating this type of bot see the article at
https://aka.ms/abs-node-luis
-----------------------------------------------------------------------------*/
"use strict";
var builder = require("botbuilder");
var botbuilder_azure = require("botbuilder-azure");
var path = require('path');

var useEmulator = (process.env.NODE_ENV == 'development');

var connector = useEmulator ? new builder.ChatConnector() : new botbuilder_azure.BotServiceConnector({
    appId: process.env['MicrosoftAppId'],
    appPassword: process.env['MicrosoftAppPassword'],
    stateEndpoint: process.env['BotStateEndpoint'],
    openIdMetadata: process.env['BotOpenIdMetadata']
});

var bot = new builder.UniversalBot(connector);
bot.localePath(path.join(__dirname, './locale'));

// Make sure you add code to validate these fields
var luisAppId = process.env.LuisAppId;
var luisAPIKey = process.env.LuisAPIKey;
var luisAPIHostName = process.env.LuisAPIHostName || 'westus.api.cognitive.microsoft.com';

const LuisModelUrl = 'https://' + luisAPIHostName + '/luis/v1/application?id=' + luisAppId + '&subscription-key=' + luisAPIKey;

// Main dialog with LUIS
var recognizer = new builder.LuisRecognizer(LuisModelUrl);
var intents = new builder.IntentDialog({ recognizers: [recognizer] })
/*
.matches('<yourIntent>')... See details at http://docs.botframework.com/builder/node/guides/understanding-natural-language/
*/
.matches('None', (session, args) => {
    session.send('Sorry, I did not understand \'%s\'.', session.message.text);
})
.matches('greeting', (session, args) => {
    session.send('Buna ziua! Despre ce tip de card doriti sa aflati informatii? Card de cumparaturi sau de debit?');
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

.onDefault((session) => {
    session.send('Sorry, I did not understand \'%s\'.', session.message.text);
});


bot.dialog('/', intents);    

if (useEmulator) {
    var restify = require('restify');
    var server = restify.createServer();
    server.listen(3978, function() {
        console.log('test bot endpont at http://localhost:3978/api/messages');
    });
    server.post('/api/messages', connector.listen());    
} else {
    module.exports = { default: connector.listen() }
}

