package pfe.example.departementservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pfe.example.departementservice.dto.DepartementDTO;
import pfe.example.departementservice.entities.Departement;
import pfe.example.departementservice.entities.DiplomeEtudier;
import pfe.example.departementservice.exception.BusinessException;
import pfe.example.departementservice.exception.ResourceNotFoundException;
import pfe.example.departementservice.mapper.DepartementMapper;
import pfe.example.departementservice.repository.DepartementRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartementService {

    private final DepartementRepository departementRepository;
    private final DepartementMapper mapper;


    // ===== CRUD Département =====

    public Departement createDepartement(Departement departement) {
        log.info("Creating department: {}", departement.getNom());

        if (departementRepository.existsByNom(departement.getNom())) {
            throw new BusinessException("Un département avec ce nom existe déjà");
        }
        return departementRepository.save(departement);
    }

    public Departement getDepartementById(Long id) {
        return departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé"));
    }

    public DepartementDTO getDepartementDtoById(Long id) {
        Departement dept = getDepartementById(id);
        return mapper.toDto(dept);
    }

    public Departement getDepartementByNom(String nom) {
        log.info("Fetching department with name: {}", nom);
        return departementRepository.findByNom(nom)
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé: " + nom));
    }

    public List<DepartementDTO> getAllDepartementsDto() {
        return departementRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }






}
