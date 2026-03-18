package com.wiss.quizbackend.service;

import com.wiss.quizbackend.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService Implementation - lädt User aus der Datenbank
 * Spring Security ruft diese Klasse auf, um User zu laden.
 * Analogie: Die Zentrale, die nachschaut, ob ein User existiert
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository){
        this.appUserRepository = appUserRepository;
    }

    /**
     * Lädt einen User anhand des Usernames aus der Datenbank
     *
     * @param username Der Username
     * @return UserDetails Object (AppUser implementiert UserDetails!)
     * @throws UsernameNotFoundException wenn User nicht existiert
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // ← Methode heißt SO (Spring-Interface)
        // ABER: man könnte auch "username" als E-Mail interpretieren!
        // Kann etwas verwirrend sein, wegen der Namensgebung vom Spring-Interface
        return appUserRepository.findByUsername(username) // ← dann würde hier die E-Mail überreicht werden
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User nicht gefunden: " + username
                ));
    }
}