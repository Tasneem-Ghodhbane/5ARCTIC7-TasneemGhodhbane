package tn.esprit.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.backend.entity.Entreprise;
import tn.esprit.backend.repository.EntrepriseRepository;
import tn.esprit.backend.service.impl.EntrepriseServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntrepriseServiceImplTest {

    @Mock
    EntrepriseRepository entrepriseRepository;

    @InjectMocks
    EntrepriseServiceImpl entrepriseService;

    @Test
    void addEntreprise() {
        Entreprise e = Entreprise.builder().id(1L).nom("Esprit").adresse("Tunis").build();
        when(entrepriseRepository.save(e)).thenReturn(e);

        assertEquals("Esprit", entrepriseService.addEntreprise(e).getNom());
        verify(entrepriseRepository).save(e);
    }

    @Test
    void getEntrepriseById_found() {
        Entreprise e = Entreprise.builder().id(1L).nom("Esprit").build();
        when(entrepriseRepository.findById(1L)).thenReturn(Optional.of(e));

        assertEquals(e, entrepriseService.getEntrepriseById(1L));
    }

    @Test
    void getEntrepriseById_notFound() {
        when(entrepriseRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(entrepriseService.getEntrepriseById(99L));
    }

    @Test
    void getAllEntreprises() {
        when(entrepriseRepository.findAll()).thenReturn(List.of(new Entreprise(), new Entreprise()));

        assertEquals(2, entrepriseService.getAllEntreprises().size());
    }

    @Test
    void deleteEntreprise() {
        entrepriseService.deleteEntreprise(1L);

        verify(entrepriseRepository).deleteById(1L);
    }
}
