function getTimestampNow() {
    const date_ob = new Date();
    const date_format = date_ob.getFullYear() + "-" + (date_ob.getMonth() + 1) + "-" +
                                date_ob.getDate() + " " + date_ob.getHours() + ":" + 
                                date_ob.getMinutes() + ":" + date_ob.getSeconds()

    return date_format
}

function log (message) {
    const log_date_format = getTimestampNow();

    if(process.env.DEBUG_DATA_FLOW && process.env.DEBUG_DATA_FLOW === "true") {
        console.log(log_date_format + " " + message);
    }
}

function error (errorMessage) {
    const log_date_format = getTimestampNow();

    if(process.env.DEBUG_DATA_FLOW && process.env.DEBUG_DATA_FLOW === "true") {
        console.error(log_date_format + " " + errorMessage);
    }
}

module.exports = {
    log, 
    error
};