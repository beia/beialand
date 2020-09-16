package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.LocationDto;
import com.beia_consult_international.solomon.exception.BeaconNotFoundException;
import com.beia_consult_international.solomon.exception.UserNotFoundException;
import com.beia_consult_international.solomon.model.*;
import com.beia_consult_international.solomon.repository.BeaconRepository;
import com.beia_consult_international.solomon.repository.LocationRepository;
import com.beia_consult_international.solomon.repository.UserBeaconTimeRepository;
import com.beia_consult_international.solomon.repository.UserRepository;
import com.beia_consult_international.solomon.service.mapper.LocationMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BeaconService {
    private final BeaconRepository beaconRepository;
    private final UserRepository userRepository;
    private final UserBeaconTimeRepository userBeaconTimeRepository;
    private final LocationRepository locationRepository;

    public BeaconService(BeaconRepository beaconRepository, UserRepository userRepository, UserBeaconTimeRepository userBeaconTimeRepository, LocationRepository locationRepository) {
        this.beaconRepository = beaconRepository;
        this.userRepository = userRepository;
        this.userBeaconTimeRepository = userBeaconTimeRepository;
        this.locationRepository = locationRepository;
    }

    public void saveBeaconTime(long userId, long beaconId, long seconds) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Beacon beacon = beaconRepository
                .findById(beaconId)
                .orElseThrow(BeaconNotFoundException::new);
        Optional<UserBeaconTime> optionalUserBeaconTime = findUserBeaconTime(user, beacon);
        if(optionalUserBeaconTime.isPresent()) {
            UserBeaconTime userBeaconTime = optionalUserBeaconTime.get();
            userBeaconTime.setSeconds(userBeaconTime.getSeconds() + seconds);
            saveUserBeaconTime(userBeaconTime);
        }
        else {
            saveUserBeaconTime(UserBeaconTime
                    .builder()
                    .user(user)
                    .beacon(beacon)
                    .seconds(seconds)
                    .build());
        }
    }

    public List<Beacon> findAll() {
        return beaconRepository.findAll();
    }

    public void saveUserBeaconTime(UserBeaconTime userBeaconTime) {
        userBeaconTimeRepository.save(userBeaconTime);
    }

    private Optional<UserBeaconTime> findUserBeaconTime(User user, Beacon beacon) {
        return userBeaconTimeRepository.findUserBeaconTimeByUserAndBeacon(user, beacon);
    }

    public LocationDto computeAndSaveLocation(BeaconLocalizationData beaconLocalizationData, long userId) {
        Location location = computeLocation(beaconLocalizationData);
        location.setUser(userRepository
                .findById(userId)
                .orElseThrow(UserNotFoundException::new));
        return LocationMapper
                .mapToDto(locationRepository.save(location));
    }

    private Location computeLocation(BeaconLocalizationData beaconLocalizationData) {
        Point[] closestBeaconsCoordinates = beaconLocalizationData.getBeaconCoordinates();
        double[] beaconDistances = beaconLocalizationData.getBeaconDistances();

        double min_x = closestBeaconsCoordinates[0].getX(),
                min_y = closestBeaconsCoordinates[0].getY(),
                min_z = closestBeaconsCoordinates[0].getZ(),
                max_x = closestBeaconsCoordinates[0].getX(),
                max_y = closestBeaconsCoordinates[0].getY(),
                max_z = closestBeaconsCoordinates[0].getZ(),
                minError = 9999999;

        for (int i = 1; i < closestBeaconsCoordinates.length; i++) {
            if(closestBeaconsCoordinates[i].getX() < min_x)
                min_x = closestBeaconsCoordinates[i].getX();
            if(closestBeaconsCoordinates[i].getY() < min_y)
                min_y = closestBeaconsCoordinates[i].getY();
            if(closestBeaconsCoordinates[i].getZ() < min_z)
                min_z = closestBeaconsCoordinates[i].getZ();
            if(closestBeaconsCoordinates[i].getX() > max_x)
                max_x = closestBeaconsCoordinates[i].getX();
            if(closestBeaconsCoordinates[i].getY() > max_y)
                max_y = closestBeaconsCoordinates[i].getY();
            if(closestBeaconsCoordinates[i].getZ() > max_z)
                max_z = closestBeaconsCoordinates[i].getZ();
        }

        double bestX = min_x, bestY = min_y, bestZ = min_z;

        for(double x = min_x; x < max_x; x++)
            for(double y = min_y; y < max_y; y++)
                for(double z = min_z; z < max_z; z++){
                    //compute error
                    double beacon0Distance = beaconDistances[0];
                    double beacon0X = closestBeaconsCoordinates[0].getX();
                    double beacon0Y = closestBeaconsCoordinates[0].getY();
                    double beacon0Z = closestBeaconsCoordinates[0].getZ();
                    double beacon0Error = Math.abs(Math.sqrt(Math.pow(x - beacon0X, 2) + Math.pow(y - beacon0Y, 2) + Math.pow(z - beacon0Z, 2)) - beacon0Distance);
                    double beacon1Distance = beaconDistances[1];
                    double beacon1X = closestBeaconsCoordinates[1].getX();
                    double beacon1Y = closestBeaconsCoordinates[1].getY();
                    double beacon1Z = closestBeaconsCoordinates[1].getZ();
                    double beacon1Error = Math.abs(Math.sqrt(Math.pow(x - beacon1X, 2) + Math.pow(y - beacon1Y, 2) + Math.pow(z - beacon1Z, 2)) - beacon1Distance);
                    double beacon2Distance = beaconDistances[2];
                    double beacon2X = closestBeaconsCoordinates[2].getX();
                    double beacon2Y = closestBeaconsCoordinates[2].getY();
                    double beacon2Z = closestBeaconsCoordinates[2].getZ();
                    double beacon2Error = Math.abs(Math.sqrt(Math.pow(x - beacon2X, 2) + Math.pow(y - beacon2Y, 2) + Math.pow(z - beacon2Z, 2)) - beacon2Distance);
                    double beacon3Distance = beaconDistances[3];
                    double beacon3X = closestBeaconsCoordinates[3].getX();
                    double beacon3Y = closestBeaconsCoordinates[3].getY();
                    double beacon3Z = closestBeaconsCoordinates[3].getZ();
                    double beacon3Error = Math.abs(Math.sqrt(Math.pow(x - beacon3X, 2) + Math.pow(y - beacon3Y, 2) + Math.pow(z - beacon3Z, 2)) - beacon3Distance);
                    double error = beacon0Error + beacon1Error + beacon2Error + beacon3Error;
                    if(error < minError) {
                        minError = error;
                        bestX = x;
                        bestY = y;
                        bestZ = z;
                    }
                }
        double bestLatitude = getLatitude(bestX, bestY, bestZ);
        double bestLongitude = getLongitude(bestX, bestY, bestZ);

        return Location
                .builder()
                .latitude(bestLatitude)
                .longitude(bestLongitude)
                .date(LocalDateTime.now())
                .build();
    }

    public List<LocationDto> findHeatmapLocations(String startDate, String endDate) {
        return locationRepository
                .findAllByDateBetween(LocalDateTime.parse(startDate),
                        LocalDateTime.parse(endDate))
                .stream()
                .map(LocationMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private double getLatitude(double x, double y, double z) {
        return 360 / (2 * Math.PI) * Math.asin(z / 6371000);
    }

    private double getLongitude(double x, double y, double z) {
        return 360 / (2 * Math.PI) * Math.atan2(y, x);
    }
}
