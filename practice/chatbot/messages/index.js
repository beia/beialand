/*-----------------------------------------------------------------------------
This template demonstrates how to use an IntentDialog with a LuisRecognizer to add
natural language support to a bot.
For a complete walkthrough of creating this type of bot see the article at
https://aka.ms/abs-node-luis
-----------------------------------------------------------------------------*/
"use strict";

let imports = {
    ChatBotApplication: require('lib/ÇhatBotApplication.class.js'),
    LuisRecognizer: require('lib/LuisRecognizer.class.js'),
    restify: require('restify'),
};

let luisConfig = {
    luisAPIKey: process.env.luisAPIKey,
    luisHostName: process.env.luisHostName,
    luisAppId: process.env.luisAppId,
};
let chatbotConfig = {
    useEmulator: (process.env.NODE_ENV == 'development'),
    connectorConfig: {
        appId: process.env['MicrosoftAppId'],
        appPassword: process.env['MicrosoftAppPassword'],
        stateEndpoint: process.env['BotStateEndpoint'],
        openIdMetadata: process.env['BotOpenIdMetadata'],
    },
};

let chatbot = new imports.ChatBotApplication(chatbotConfig);
let luis = new imports.LuisRecognizer(luisConfig);
chatbot.addRecognizer(luis);
chatbot.initBot();

// TODO: this below is ugly, fix it
if (chatbotConfig.useEmulator) {
    let server = imports.restify.createServer();
    server.listen(3978, function() {
        console.log('test bot endpont at http://localhost:3978/api/messages');
    });
    server.post('/api/messages', chatbot.getEndpoint());
} else {
    module.exports = { default: chatbot.getEndpoint() };
}

