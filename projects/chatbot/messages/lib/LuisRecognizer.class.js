'use strict';

let imports = {
    builder: require("botbuilder"),
};

module.exports = class LuisRecognizer {
    constructor(config) {
        super();
        this.luisHostName = config['hostName'] || 'westus.api.cognitive.microsoft.com';
        if (!config['luisAPIKey']) {
            throw new Error('luisAPIKey not initialized!');
        }
        this.luisAPIKey = config['luisAPIKey'];
        if (!config['luisAppId']) {
            throw new Error('luisAppId not initialized!');
        }
        this.luisAppId = config['luisAppId'];
        this.initRecognizer();
    }
    getUrl() {
        return 'https://' + this.luisAPIHostName + '/luis/v1/application?id=' + this.luisAppId + '&subscription-key=' + this.luisAPIKey;
    }
    getRecognizer() {
        return this.recognizer;
    }
    initRecognizer() {
        this.recognizer = new builder.LuisRecognizer(this.getUrl());
    }
};