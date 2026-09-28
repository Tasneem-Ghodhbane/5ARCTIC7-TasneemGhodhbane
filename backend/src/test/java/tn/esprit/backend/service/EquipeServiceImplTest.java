package tn.esprit.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.entity.Equipe;
import tn.esprit.backend.entity.Projet;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.repository.EquipeRepository;
import tn.esprit.backend.repository.ProjetRepository;
import tn.esprit.backend.service.impl.EquipeServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipeServiceImplTest {

    @Mock
    EquipeRepository equipeRepository;
    @Mock
    EntrepriseRepository entrepriseRepository;
    @Mock
    ProjetRepository projetRepository;

    @InjectMocks
    EquipeServiceImpl equipeService;

    private Equipe newEquipe() {
        return Equipe.builder().id(1L).nom("Team A").specialite("Backend")
                .projets(new ArrayList<>()).build();
    }

    @Test
    void addEquipe() {
        Equipe equipe = newEquipe();
        when(equipeRepository.save(equipe)).thenReturn(equipe);

        assertEquals("Team A", equipeService.addEquipe(equipe).getNom());
    }

    @Test
    void getEquipeById() {
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(newEquipe()));

        assertEquals("Backend", equipeService.getEquipeById(1L).getSpecialite());
    }

    @Test
    void getEquipesByEntreprise() {
        when(equipeRepository.findByEntrepriseId(10L)).thenReturn(List.of(newEquipe()));

        assertEquals(1, equipeService.getEquipesByEntreprise(10L).size());
    }

    @Test
    void assignEquipeToEntreprise() {
        Equipe equipe = newEquipe();
        Entreprise entreprise = Entreprise.builder().id(10L).nom("Esprit").build();
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));
        when(entrepriseRepository.findById(10L)).thenReturn(Optional.of(entreprise));
        when(equipeRepository.save(any(Equipe.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals(entreprise, equipeService.assignEquipeToEntreprise(1L, 10L).getEntreprise());
    }

    @Test
    void assignEquipeToProjet() {
        Equipe equipe = newEquipe();
        Projet projet = Projet.builder().id(20L).sujet("DevOps").build();
        when(equipeRepository.findById(1L)).thenReturn(Optional.of(equipe));
        when(projetRepository.findById(20L)).thenReturn(Optional.of(projet));
        when(equipeRepository.save(any(Equipe.class))).thenAnswer(inv -> inv.getArgument(0));

        assertTrue(equipeService.assignEquipeToProjet(1L, 20L).getProjets().contains(projet));
    }
}
