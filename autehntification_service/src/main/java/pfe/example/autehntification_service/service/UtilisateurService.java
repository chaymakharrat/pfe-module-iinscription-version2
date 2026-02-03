package pfe.example.autehntification_service.service;

import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pfe.example.autehntification_service.entities.Utilisateur;
import pfe.example.autehntification_service.repository.UtilisateurRepository;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UtilisateurService implements UserDetailsService {
    final UtilisateurRepository utilisateurRepository;
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Utilisateur appUser = utilisateurRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + login));

        return org.springframework.security.core.userdetails.User
                .withUsername(appUser.getLogin())
                .password(appUser.getPassword())
                .authorities("SCOPE_" + appUser.getRole().name())// ex: ROLE_USER
                .build();
    }
}
