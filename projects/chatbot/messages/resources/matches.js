'use strict';

let imports = {
    MatchRule: require('lib/MatchRule.class.js'),
};

module.exports = function(context) {
    context.intents.matches('None',
        new MatchRule({
            controller: 'NotFoundController',
            action: 'notFound',
            context: context,
        }).getHandler());
    // ...
};