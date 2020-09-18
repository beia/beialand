package com.beia_consult_international.solomon.dto;

import lombok.Builder;

@Builder
public class ParkingStatsDto {
  private String LW_EUI;
  private String LW_ts;
  private int LW_up_counter;
  private int LW_port;
  private int LW_rssi;
  private String LW_dr;
  private String LW_uplink;
  private String LW_downlink;
  private int frame_type_uplink;
  private int parking_slot_status;
  private int battery_state;
  private int recalibration;
  private int sequence_number;
  private int sensor_error;
  private int temperature;
  private int time_hh;
  private int time_mm;
  private int distance;
  private int amplitude;
  private int n_reflections;
  private int battery;
  private int radar_threshold;
  private int radar_range_start;
  private int radar_range_length;
  private  int firmware_version;
  private int sleep_time_minutes;
  private int sleep_time_seconds;
  private int keep_alive;
  private int nm;
  private int nm_start_hour;
  private int nm_duration;
  private int nm_sleep_time;
  private int nm_keep_alive;
  private int lost_up_counter;
  private int lost_seq;

    public ParkingStatsDto(String LW_EUI, String LW_ts, int LW_up_counter, int LW_port, int LW_rssi, String LW_dr, String LW_uplink, String LW_downlink, int frame_type_uplink, int parking_slot_status, int battery_state, int recalibration, int sequence_number, int sensor_error, int temperature, int time_hh, int time_mm, int distance, int amplitude, int n_reflections, int battery, int radar_threshold, int radar_range_start, int radar_range_length, int firmware_version, int sleep_time_minutes, int sleep_time_seconds, int keep_alive, int nm, int nm_start_hour, int nm_duration, int nm_sleep_time, int nm_keep_alive, int lost_up_counter, int lost_seq) {
        this.LW_EUI = LW_EUI;
        this.LW_ts = LW_ts;
        this.LW_up_counter = LW_up_counter;
        this.LW_port = LW_port;
        this.LW_rssi = LW_rssi;
        this.LW_dr = LW_dr;
        this.LW_uplink = LW_uplink;
        this.LW_downlink = LW_downlink;
        this.frame_type_uplink = frame_type_uplink;
        this.parking_slot_status = parking_slot_status;
        this.battery_state = battery_state;
        this.recalibration = recalibration;
        this.sequence_number = sequence_number;
        this.sensor_error = sensor_error;
        this.temperature = temperature;
        this.time_hh = time_hh;
        this.time_mm = time_mm;
        this.distance = distance;
        this.amplitude = amplitude;
        this.n_reflections = n_reflections;
        this.battery = battery;
        this.radar_threshold = radar_threshold;
        this.radar_range_start = radar_range_start;
        this.radar_range_length = radar_range_length;
        this.firmware_version = firmware_version;
        this.sleep_time_minutes = sleep_time_minutes;
        this.sleep_time_seconds = sleep_time_seconds;
        this.keep_alive = keep_alive;
        this.nm = nm;
        this.nm_start_hour = nm_start_hour;
        this.nm_duration = nm_duration;
        this.nm_sleep_time = nm_sleep_time;
        this.nm_keep_alive = nm_keep_alive;
        this.lost_up_counter = lost_up_counter;
        this.lost_seq = lost_seq;
    }

    public String getLW_EUI() {
        return LW_EUI;
    }

    public void setLW_EUI(String LW_EUI) {
        this.LW_EUI = LW_EUI;
    }

    public String getLW_ts() {
        return LW_ts;
    }

    public void setLW_ts(String LW_ts) {
        this.LW_ts = LW_ts;
    }

    public int getLW_up_counter() {
        return LW_up_counter;
    }

    public void setLW_up_counter(int LW_up_counter) {
        this.LW_up_counter = LW_up_counter;
    }

    public int getLW_port() {
        return LW_port;
    }

    public void setLW_port(int LW_port) {
        this.LW_port = LW_port;
    }

    public int getLW_rssi() {
        return LW_rssi;
    }

    public void setLW_rssi(int LW_rssi) {
        this.LW_rssi = LW_rssi;
    }

    public String getLW_dr() {
        return LW_dr;
    }

    public void setLW_dr(String LW_dr) {
        this.LW_dr = LW_dr;
    }

    public String getLW_uplink() {
        return LW_uplink;
    }

    public void setLW_uplink(String LW_uplink) {
        this.LW_uplink = LW_uplink;
    }

    public String getLW_downlink() {
        return LW_downlink;
    }

    public void setLW_downlink(String LW_downlink) {
        this.LW_downlink = LW_downlink;
    }

    public int getFrame_type_uplink() {
        return frame_type_uplink;
    }

    public void setFrame_type_uplink(int frame_type_uplink) {
        this.frame_type_uplink = frame_type_uplink;
    }

    public int getParking_slot_status() {
        return parking_slot_status;
    }

    public void setParking_slot_status(int parking_slot_status) {
        this.parking_slot_status = parking_slot_status;
    }

    public int getBattery_state() {
        return battery_state;
    }

    public void setBattery_state(int battery_state) {
        this.battery_state = battery_state;
    }

    public int getRecalibration() {
        return recalibration;
    }

    public void setRecalibration(int recalibration) {
        this.recalibration = recalibration;
    }

    public int getSequence_number() {
        return sequence_number;
    }

    public void setSequence_number(int sequence_number) {
        this.sequence_number = sequence_number;
    }

    public int getSensor_error() {
        return sensor_error;
    }

    public void setSensor_error(int sensor_error) {
        this.sensor_error = sensor_error;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getTime_hh() {
        return time_hh;
    }

    public void setTime_hh(int time_hh) {
        this.time_hh = time_hh;
    }

    public int getTime_mm() {
        return time_mm;
    }

    public void setTime_mm(int time_mm) {
        this.time_mm = time_mm;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(int amplitude) {
        this.amplitude = amplitude;
    }

    public int getN_reflections() {
        return n_reflections;
    }

    public void setN_reflections(int n_reflections) {
        this.n_reflections = n_reflections;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getRadar_threshold() {
        return radar_threshold;
    }

    public void setRadar_threshold(int radar_threshold) {
        this.radar_threshold = radar_threshold;
    }

    public int getRadar_range_start() {
        return radar_range_start;
    }

    public void setRadar_range_start(int radar_range_start) {
        this.radar_range_start = radar_range_start;
    }

    public int getRadar_range_length() {
        return radar_range_length;
    }

    public void setRadar_range_length(int radar_range_length) {
        this.radar_range_length = radar_range_length;
    }

    public int getFirmware_version() {
        return firmware_version;
    }

    public void setFirmware_version(int firmware_version) {
        this.firmware_version = firmware_version;
    }

    public int getSleep_time_minutes() {
        return sleep_time_minutes;
    }

    public void setSleep_time_minutes(int sleep_time_minutes) {
        this.sleep_time_minutes = sleep_time_minutes;
    }

    public int getSleep_time_seconds() {
        return sleep_time_seconds;
    }

    public void setSleep_time_seconds(int sleep_time_seconds) {
        this.sleep_time_seconds = sleep_time_seconds;
    }

    public int getKeep_alive() {
        return keep_alive;
    }

    public void setKeep_alive(int keep_alive) {
        this.keep_alive = keep_alive;
    }

    public int getNm() {
        return nm;
    }

    public void setNm(int nm) {
        this.nm = nm;
    }

    public int getNm_start_hour() {
        return nm_start_hour;
    }

    public void setNm_start_hour(int nm_start_hour) {
        this.nm_start_hour = nm_start_hour;
    }

    public int getNm_duration() {
        return nm_duration;
    }

    public void setNm_duration(int nm_duration) {
        this.nm_duration = nm_duration;
    }

    public int getNm_sleep_time() {
        return nm_sleep_time;
    }

    public void setNm_sleep_time(int nm_sleep_time) {
        this.nm_sleep_time = nm_sleep_time;
    }

    public int getNm_keep_alive() {
        return nm_keep_alive;
    }

    public void setNm_keep_alive(int nm_keep_alive) {
        this.nm_keep_alive = nm_keep_alive;
    }

    public int getLost_up_counter() {
        return lost_up_counter;
    }

    public void setLost_up_counter(int lost_up_counter) {
        this.lost_up_counter = lost_up_counter;
    }

    public int getLost_seq() {
        return lost_seq;
    }

    public void setLost_seq(int lost_seq) {
        this.lost_seq = lost_seq;
    }
}
