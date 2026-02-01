//package pfe.example.enrollement_module.workflow;
//
//import org.springframework.beans.factory.annotation.*;
//import org.springframework.stereotype.Component;
//import pfe.example.enrollement_module.client.EtudiantRestClient;
//import pfe.example.enrollement_module.entities.DemandeInscription;
//import pfe.example.enrollement_module.repository.DemandeInscriptionRepository;
//
//public class delegate {
//     ConvertToStudentDelegate.java dans ENROLLMENT-SERVICE
//    @Component("convertToStudentDelegate")
//    public class ConvertToStudentDelegate implements JavaDelegate {
//
//        @Autowired
//        private DemandeInscriptionRepository demandeInscriptionRepository;
//
//        @Autowired
//        private EtudiantRestClient etudiantRestClient;
//
//        @Autowired
//        private AuthClient authClient;
//
//        @Override
//        public void execute(DelegateExecution execution) {
//            Long candidatId = (Long) execution.getVariable("candidatId");
//
//            // 1. Récupérer le candidat
//            Candidat candidat = candidatRepository.findById(candidatId)
//                    .orElseThrow();
//
//            // 2. Créer le compte utilisateur (AUTH-SERVICE)
//            UserDTO user = authClient.createStudent(new CreateUserRequest(
//                    candidat.getEmail(),
//                    candidat.getNom(),
//                    candidat.getPrenom(),
//                    UserRole.STUDENT
//            ));
//
//            // 3. Créer l'étudiant (STUDENT-SERVICE)
//            EtudiantDTO etudiant = studentClient.createFromCandidat(
//                    new CreateEtudiantRequest(
//                            candidat.getId(),
//                            user.getId(),
//                            "2024-2025",  // Année scolaire
//                            candidat.getDiplomeSouhaite().getId()
//                    )
//            );
//
//            // 4. Mettre à jour le statut du candidat
//            candidat.setStatut(StatutDemandeInscription.INSCRIT);
//            candidat.setDateAcceptation(LocalDateTime.now());
//            candidatRepository.save(candidat);
//
//            // 5. Stocker les infos dans le workflow
//            execution.setVariable("etudiantId", etudiant.getId());
//            execution.setVariable("matricule", etudiant.getMatricule());
//        }
//    }
//}
