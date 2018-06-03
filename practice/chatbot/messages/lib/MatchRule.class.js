'use strict';

module.exports = class MatchRule {
    constructor(config){
        super();
        if (!config.controller) {
            throw new Error('You need to define a controller atribute');
        }
        this.controllerClass = require('controllers/' + config.controller + '.class.js');
        this.action = config.action;
        this.context = config.context;
    }
    getHandler() {
        return (session, args) => this.handler(session, args);
    }
    handler(session, args) {
        let controller = new this.controllerClass();
        controller.setContext(this.context);
        // TODO: count calls, time, ... ??!?!?
        // TODO: exception handling
        // TODO: default message if it doesn't respond in due time
        return controller[this.action].apply(controller, session, args);
    }
};