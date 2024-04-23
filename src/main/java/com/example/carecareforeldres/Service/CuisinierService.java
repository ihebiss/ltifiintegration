package com.example.carecareforeldres.Service;

import com.example.carecareforeldres.Entity.Cuisinier;
import com.example.carecareforeldres.Entity.Restaurant;
import com.example.carecareforeldres.Entity.TypeBadge;
import com.example.carecareforeldres.Repository.CuisinierRepository;
import com.example.carecareforeldres.Repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CuisinierService implements IServiceCuisinier{
    CuisinierRepository cuisinierRepository;
    RestaurantRepository restaurantRepository;



    public List<Cuisinier> getAllUSers (){
        return this.cuisinierRepository.findAll();
    }
    @Override
    public Cuisinier add(Cuisinier res) {
        res.setScore(4);
        res.setDisponiblee(Boolean.FALSE);
        res.setDateAjout(LocalDate.now());

        return cuisinierRepository.save(res);}
    @Override
    public List<Cuisinier> getAll(){return cuisinierRepository.findAll();}

    @Override
    public void remove(int idf) {
        cuisinierRepository.deleteById(idf);}

    @Override
    public Cuisinier update(Cuisinier res) {
        return cuisinierRepository.save(res);
    }


    @Scheduled(cron = "0 59 23 L * *")
    public void mettreAJourBadges() {


        for(Restaurant restaurant:restaurantRepository.findAll()) {
            List<Cuisinier> cuisiniers = restaurant.getCuisiniers();
            Cuisinier chefAvecPlusDePlats = cuisiniers.stream().max(Comparator.comparingInt(c -> c.getPlats().size())).orElse(null);

            if (chefAvecPlusDePlats != null) {
                chefAvecPlusDePlats.setScore(chefAvecPlusDePlats.getScore() + 15);
                cuisinierRepository.save(chefAvecPlusDePlats);
            }

            int scoreMax = cuisiniers.stream().mapToInt(Cuisinier::getScore).max().orElse(0);

            for (Cuisinier c : cuisiniers) {
                if (c.getScore() == scoreMax) {
                    c.setDisponiblee(Boolean.TRUE);
                    c.setSalaire((float) (c.getSalaire() * 1.1));
                    cuisinierRepository.save(c);
                } else {
                    c.setDisponiblee(Boolean.FALSE);
                }
            }

            for (Cuisinier c : cuisiniers) {
                int score = c.getScore();
                if (score <= 20) {
                    c.setTypeBadge(TypeBadge.BRONZE);
                    c.setScore(1);
                } else if (score < 30) {
                    c.setTypeBadge(TypeBadge.SILVER);
                    c.setScore(2);
                } else {
                    c.setTypeBadge(TypeBadge.GOLD);
                    c.setScore(4);
                }
                cuisinierRepository.save(c);
            }
        }
    }






}
