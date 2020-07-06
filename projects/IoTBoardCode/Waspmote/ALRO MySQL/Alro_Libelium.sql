# Date row
SELECT timestamp, id_wasp, sensor, value FROM sensorParser WHERE timestamp >= '2018-07-16' and (id_wasp in ("SCP1", "SCP2", "SCP4")) and (sensor in ("TC", "PRES", "HUM", "PM1", "PM10", "PM2_5"))

# Medii orare
SELECT DATE(timestamp) as "ZI", HOUR(timestamp) as "ORA", id_wasp, sensor, count(*) as "Numar_valori", avg(value) as "Medie orara" FROM sensorParser WHERE timestamp >= '2018-07-16' and (id_wasp in ("SCP1", "SCP2", "SCP4")) and (sensor in ("TC", "PRES", "HUM", "PM1", "PM10", "PM2_5")) group by DATE(timestamp), HOUR(timestamp), id_wasp, sensor order by DATE(timestamp), id_wasp, sensor, HOUR(timestamp)

# Medii zilnice
SELECT DATE(timestamp) as "ZI", id_wasp, sensor, count(*) as "Numar_valori", avg(value) as "Medie Zilnica" FROM sensorParser WHERE timestamp >= '2018-07-16' and (id_wasp in ("SCP1", "SCP2", "SCP4")) and (sensor in ("TC", "PRES", "HUM", "PM1", "PM10", "PM2_5")) group by DATE(timestamp), id_wasp, sensor order by DATE(timestamp), id_wasp, sensor
