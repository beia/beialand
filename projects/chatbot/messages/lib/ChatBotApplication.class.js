'use strict';

let imports = {
    builder: require("botbuilder"),
    botbuilder_azure: require("botbuilder-azure"),
    path: require("path"),
    Iban: require("lib/models/Iban.class.js"),
};

module.exports = class ChatBotApplication {
    constructor(config) {
        super();
        this.useEmulator = !!config['useEmulator'];
        this.connectorConfig = config['connectorConfig'];
        this.initFramework();
        this.initBot();
        this.initModels();
    }
    initFramework() {
        if (this.useEmulator) {
            this.connector = new builder.ChatConnector();
        } else {
            this.connector = new botbuilder_azure.BotServiceConnector(this.connectorConfig);
        }
    }
    addRecognizer(recognizer) {
        if (!this.recognizers) {
            this.recognizers = [];
        }
        this.recognizers.push(recognizer);
    }
    initBot() {
        if (!this.recognizers) {
            throw new Error("You have no recognizers in your app");
        }
        this.intents = new builder.IntentDialog({ recognizers: [recognizer] });
        let matches = require('resources/matches.js');
        matches(this);
        this.bot = new builder.UniversalBot(this.connector);
        this.bot.localePath(path.join(__dirname, './locale'));
        this.bot.dialog('/', this.intents);
        this.ready = true;
    }
    initModels() {
        this.account = new imports.Iban();
    }
    getEndpoint() {
        if (!this.ready) {
            throw new Error("You to properly call initBot before this function!");
        }
        return this.connector.listen();
    }
};