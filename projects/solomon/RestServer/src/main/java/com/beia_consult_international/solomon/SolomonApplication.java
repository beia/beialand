package com.beia_consult_international.solomon;

import com.beia_consult_international.solomon.model.*;
import com.beia_consult_international.solomon.repository.*;
import com.beia_consult_international.solomon.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class SolomonApplication implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MallRepository mallRepository;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private BeaconRepository beaconRepository;
    @Autowired
    private ParkingSpaceRepository parkingSpaceRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SolomonApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        List<User> users = List.of(
                User.builder()//0
                        .username("admin")
                        .password(passwordEncoder.encode("solomon"))
                        .firstName("Admin")
                        .role(Role.ADMIN)
                        .build(),
                User.builder()//1
                        .username("McDonald's")
                        .password(passwordEncoder.encode("mc"))
                        .firstName("McDonald's")
                        .role(Role.RETAILER)
                        .build(),
                User.builder()//2
                        .username("Altex")
                        .password(passwordEncoder.encode("altex"))
                        .firstName("Altex")
                        .role(Role.RETAILER)
                        .build(),
                User.builder()//3
                        .username("Zara")
                        .password(passwordEncoder.encode("zara"))
                        .firstName("Zara")
                        .role(Role.RETAILER)
                        .build(),
                User.builder()//4
                        .username("Starbucks")
                        .password(passwordEncoder.encode("starbucks"))
                        .firstName("Starbucks")
                        .role(Role.RETAILER)
                        .build(),
                User.builder()//5
                        .username("Emag")
                        .password(passwordEncoder.encode("emag"))
                        .firstName("Emag")
                        .role(Role.RETAILER)
                        .build(),
                User.builder()//6
                        .username("Nike")
                        .password(passwordEncoder.encode("nike"))
                        .firstName("Nike")
                        .role(Role.RETAILER)
                        .build(),
                User.builder()//7
                        .username("Taco Bell")
                        .password(passwordEncoder.encode("taco bell"))
                        .firstName("Taco Bell")
                        .role(Role.RETAILER)
                        .build(),
                User.builder()//8
                        .username("Bershka")
                        .password(passwordEncoder.encode("bershka"))
                        .firstName("Bershka")
                        .role(Role.RETAILER)
                        .build(),
                User.builder()//9
                        .username("beia")
                        .password(passwordEncoder.encode("beiatapi"))
                        .firstName("Beia Consult International")
                        .role(Role.MALL)
                        .build(),
                User.builder()//10
                        .username("baneasa")
                        .password(passwordEncoder.encode("baneasa"))
                        .firstName("Baneasa Shopping City")
                        .role(Role.MALL)
                        .build(),
                User.builder()//11
                        .username("promenada")
                        .password(passwordEncoder.encode("promenada"))
                        .firstName("Promenada Mall")
                        .role(Role.MALL)
                        .build(),
                User.builder()//12
                        .username("veranda")
                        .password(passwordEncoder.encode("veranda"))
                        .firstName("Veranda Mall")
                        .role(Role.MALL)
                        .build(),
                User.builder()//13
                        .username("izvor")
                        .password(passwordEncoder.encode("izvor"))
                        .firstName("Izvor")
                        .role(Role.MALL)
                        .build(),
                User.builder()//14
                        .username("agent")
                        .password(passwordEncoder.encode("agent"))
                        .firstName("Agent")
                        .lastName("1")
                        .role(Role.AGENT)
                        .build());

        userRepository.saveAll(users);


        List<Mall> malls = List.of(
                Mall.builder()
                        .name("Beia Consult International")
                        .latitude(44.3957038)
                        .longitude(26.1027026)
                        .user(users.get(9))
                        .build(),
                Mall.builder()
                        .name("Baneasa Shopping City")
                        .latitude(44.508874)
                        .longitude(26.087669)
                        .user(users.get(10))
                        .build(),
                Mall.builder()
                        .name("Promenada Mall")
                        .latitude(44.478531)
                        .longitude(26.102645)
                        .user(users.get(11))
                        .build(),
                Mall.builder()
                        .name("Veranda Mall")
                        .latitude(44.452251)
                        .longitude(26.130569)
                        .user(users.get(12))
                        .build(),
                Mall.builder()
                        .name("Izvor")
                        .latitude(44.436242)
                        .longitude(26.086765)
                        .user(users.get(13))
                        .build());
        mallRepository.saveAll(malls);


        List<Beacon> beacons = List.of(
                Beacon.builder()//0
                        .type(BeaconType.NORMAL)
                        .manufacturerId("LKhV")
                        .name("Starbucks")
                        .latitude(44.4364283)
                        .longitude(26.0868782)
                        .major(41302)
                        .minor(22282)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(4))
                        .user(users.get(4))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//1
                        .type(BeaconType.NORMAL)
                        .manufacturerId("MTFa")
                        .name("Taco Bell")
                        .latitude(44.395706)
                        .longitude(26.10266)
                        .major(59730)
                        .minor(3532)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(0))
                        .user(users.get(7))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//2
                        .type(BeaconType.NORMAL)
                        .manufacturerId("PcNy")
                        .name("Nike")
                        .latitude(44.3957388)
                        .longitude(26.1027663)
                        .major(58450)
                        .minor(19566)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(0))
                        .user(users.get(6))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//3
                        .type(BeaconType.NORMAL)
                        .manufacturerId("R4JH")
                        .name("Zara")
                        .latitude(44.4363632)
                        .longitude(26.0868611)
                        .major(24334)
                        .minor(57021)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(4))
                        .user(users.get(3))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//4
                        .type(BeaconType.NORMAL)
                        .manufacturerId("rrZd")
                        .name("McDonald's")
                        .latitude(44.4364185)
                        .longitude(26.0869449)
                        .major(39824)
                        .minor(22135)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(4))
                        .user(users.get(1))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//5
                        .type(BeaconType.NORMAL)
                        .manufacturerId("tZF7")
                        .name("Emag")
                        .latitude(44.3956626)
                        .longitude(26.1026764)
                        .major(3204)
                        .minor(63655)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(0))
                        .user(users.get(5))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//6
                        .type(BeaconType.NORMAL)
                        .manufacturerId("v7mz")
                        .name("Bershka")
                        .latitude(44.3956943)
                        .longitude(26.1027958)
                        .major(25098)
                        .minor(4628)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(0))
                        .user(users.get(8))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//7
                        .type(BeaconType.NORMAL)
                        .manufacturerId("zbCe")
                        .name("Altex")
                        .latitude(44.4363572)
                        .longitude(26.0869305)
                        .major(60181)
                        .minor(7706)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(4))
                        .user(users.get(2))
                        .manufacturer("KONTAKT")
                        .build(),

                Beacon.builder()//8
                        .type(BeaconType.PARKING)
                        .manufacturerId("xxxx")
                        .name("Beia Parking Space 1")
                        .latitude(44.4363572)
                        .longitude(26.0869305)
                        .major(60181)
                        .minor(7706)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(0))
                        .user(users.get(0))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//9
                        .type(BeaconType.PARKING)
                        .manufacturerId("xxxx")
                        .name("Beia Parking Space 2")
                        .latitude(44.4363572)
                        .longitude(26.0869305)
                        .major(60181)
                        .minor(7706)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(0))
                        .user(users.get(0))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//10
                        .type(BeaconType.PARKING)
                        .manufacturerId("xxxx")
                        .name("Beia Parking Space 3")
                        .latitude(44.4363572)
                        .longitude(26.0869305)
                        .major(60181)
                        .minor(7706)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(0))
                        .user(users.get(0))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//11
                        .type(BeaconType.PARKING)
                        .manufacturerId("xxxx")
                        .name("Beia Parking Space 4")
                        .latitude(44.4363572)
                        .longitude(26.0869305)
                        .major(60181)
                        .minor(7706)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(0))
                        .user(users.get(0))
                        .manufacturer("KONTAKT")
                        .build(),
                Beacon.builder()//12
                        .type(BeaconType.PARKING)
                        .manufacturerId("xxxx")
                        .name("Beia Parking Space 5")
                        .latitude(44.4363572)
                        .longitude(26.0869305)
                        .major(60181)
                        .minor(7706)
                        .layer(1)
                        .floor(0)
                        .mall(malls.get(0))
                        .user(users.get(0))
                        .manufacturer("KONTAKT")
                        .build());
        beaconRepository.saveAll(beacons);

        //BEIA CAMPAIGNS
        List<Campaign> campaignsBeia = List.of(
                Campaign
                        .builder()
                        .title("Adidasi nike")
                        .description("pret: 500 lei")
                        .category(Category.Shoes)
                        .startDate(LocalDateTime.now().minusDays(10))
                        .endDate(LocalDateTime.now().minusDays(1))
                        .user(users.get(6))
                        .build(),
                Campaign
                        .builder()
                        .title("Iphone 11 pro")
                        .description("pret: 4500 lei")
                        .category(Category.Smartphones)
                        .startDate(LocalDateTime.now().minusDays(10))
                        .endDate(LocalDateTime.now().plusDays(50))
                        .user(users.get(5))
                        .build(),
                Campaign
                        .builder()
                        .title("Taco bell oferta 1")
                        .description("pret: 35 lei")
                        .category(Category.Food)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(7))
                        .build(),
                Campaign
                        .builder()
                        .title("SmartTv Samsung")
                        .description("pret: 3000 lei")
                        .category(Category.Electronics)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(5))
                        .build(),
                Campaign
                        .builder()
                        .title("Tricou dama")
                        .description("pret: 60 lei")
                        .category(Category.Clothes)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(8))
                        .build(),
                Campaign
                        .builder()
                        .title("Sapca Nike")
                        .description("pret: 150 lei")
                        .category(Category.Clothes)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(6))
                        .build()
        );

        //IZVOR CAMPAIGNS
        List<Campaign> campaignsIzvor = List.of(
                Campaign
                        .builder()
                        .title("Adidasi nike")
                        .description("pret: 500 lei")
                        .category(Category.Shoes)
                        .startDate(LocalDateTime.now().minusDays(1))
                        .endDate(LocalDateTime.now().plusDays(100))
                        .user(users.get(4))
                        .build(),
                Campaign
                        .builder()
                        .title("Iphone 11 pro")
                        .description("pret: 4500 lei")
                        .category(Category.Smartphones)
                        .startDate(LocalDateTime.now().minusDays(10))
                        .endDate(LocalDateTime.now().plusDays(50))
                        .user(users.get(2))
                        .build(),
                Campaign
                        .builder()
                        .title("MC oferta test")
                        .description("pret: 35 lei")
                        .category(Category.Food)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(1))
                        .build(),
                Campaign
                        .builder()
                        .title("SmartTv Samsung")
                        .description("pret: 3000 lei")
                        .category(Category.Electronics)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(2))
                        .build(),
                Campaign
                        .builder()
                        .title("Tricou dama")
                        .description("pret: 60 lei")
                        .category(Category.Clothes)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(3))
                        .build(),
                Campaign
                        .builder()
                        .title("Sapca Nike")
                        .description("pret: 150 lei")
                        .category(Category.Clothes)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(3))
                        .build()
        );


        //BEIA PARKING SPACES
        List<ParkingSpace> parkingSpaces = List.of(
                ParkingSpace
                        .builder()
                        .UID("0004A30B00EB0782")
                        .latitude(44.395571)
                        .longitude(26.102969)
                        .rotation(68)
                        .mall(malls.get(0))
                        .beacon(beacons.get(8))
                        .parkingData(List.of(
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(5))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(4))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.OCCUPIED)
                                        .date(LocalDateTime.now().minusMinutes(3))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(2))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(1))
                                        .build()
                        ))
                        .build(),
                ParkingSpace
                        .builder()
                        .UID("0004A30B00EB04FD")
                        .latitude(44.395564)
                        .longitude(26.102829)
                        .rotation(-30)
                        .beacon(beacons.get(9))
                        .mall(malls.get(0))
                        .parkingData(List.of(
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(5))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.OCCUPIED)
                                        .date(LocalDateTime.now().minusMinutes(4))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(3))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(2))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.OCCUPIED)
                                        .date(LocalDateTime.now().minusMinutes(1))
                                        .build()
                        ))
                        .build(),
                ParkingSpace
                        .builder()
                        .UID("0004A30B00EB71F9")
                        .latitude(44.395574)
                        .longitude(26.102736)
                        .rotation(90)
                        .mall(malls.get(0))
                        .beacon(beacons.get(10))
                        .parkingData(List.of(
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(5))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.OCCUPIED)
                                        .date(LocalDateTime.now().minusMinutes(4))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(3))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.OCCUPIED)
                                        .date(LocalDateTime.now().minusMinutes(2))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.OCCUPIED)
                                        .date(LocalDateTime.now().minusMinutes(1))
                                        .build()
                        ))
                        .build(),
                ParkingSpace
                        .builder()
                        .UID("0004A30B00EB5C67")
                        .latitude(44.395560)
                        .longitude(26.102681)
                        .rotation(90)
                        .beacon(beacons.get(11))
                        .mall(malls.get(0))
                        .parkingData(List.of(
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.OCCUPIED)
                                        .date(LocalDateTime.now().minusMinutes(5))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(4))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(3))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(2))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(1))
                                        .build()
                        ))
                        .build(),
                ParkingSpace
                        .builder()
                        .UID("0004A30B00EB0E1E")
                        .latitude(44.395615)
                        .longitude(26.102889)
                        .rotation(68)
                        .mall(malls.get(0))
                        .beacon(beacons.get(10))
                        .parkingData(List.of(
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(5))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(4))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(3))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.FREE)
                                        .date(LocalDateTime.now().minusMinutes(2))
                                        .build(),
                                ParkingData
                                        .builder()
                                        .status(ParkingStatus.OCCUPIED)
                                        .date(LocalDateTime.now().minusMinutes(1))
                                        .build()
                        ))
                        .build()
        );
        parkingSpaces.forEach(parkingSpace -> parkingSpace
                .getParkingData()
                .forEach(parkingData -> parkingData.setParkingSpace(parkingSpace)));

        campaignService.saveAll(campaignsBeia);
        campaignService.saveAll(campaignsIzvor);
        parkingSpaceRepository.saveAll(parkingSpaces);


    }
}
