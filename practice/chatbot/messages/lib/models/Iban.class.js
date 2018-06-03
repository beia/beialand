'use strict';

module.exports = class Numar {
    constructor() {
        this.initIban();
    }
    initIban(){
        this.cnp = {
            "29876": { iban: "RO123456789", sold: "3243 RON" },
            "29776": { iban: "RO987456321", sold: "569875 RON" },
            "29676": { iban: "RO741852963", sold: "3654 RON" },
        };
    }
    getIban(person, cb){
        if(!this.ibans[person]){
            return cb(new Error("Person not found"), null);
        }
        return cb(null, this.cnp[person].iban);
    }
    getSold(person, cb){
        if(!this.ibans[person]){
            return cb(new Error("Person not found"), null);
        }
        return cb(null, this.cnp[person].sold);
    }
};