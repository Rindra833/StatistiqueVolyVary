package com.Hasner.VolyVary.controller;

import com.Hasner.VolyVary.model.MouvementProjet;
import com.Hasner.VolyVary.model.NatureMouvement;
import com.Hasner.VolyVary.model.Projet;
import com.Hasner.VolyVary.repository.MouvementProjetRepository;
import com.Hasner.VolyVary.repository.ProjetRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
public class StatistiqueController {

    private static final DateTimeFormatter FORM_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ProjetRepository projetRepository;
    private final MouvementProjetRepository mouvementRepository;

    public StatistiqueController(ProjetRepository projetRepository,
                                 MouvementProjetRepository mouvementRepository) {
        this.projetRepository = projetRepository;
        this.mouvementRepository = mouvementRepository;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails,
                       @RequestParam(value = "projetId", required = false) Long projetId,
                       @RequestParam(value = "dateDebut", required = false) String dateDebut,
                       @RequestParam(value = "dateFin", required = false) String dateFin,
                       Model model) {
        String username = userDetails.getUsername();
        List<Projet> projets = projetRepository.findByUtilisateurNomOrderByNomAsc(username);
        LocalDate today = LocalDate.now();
        LocalDate defaultEnd = today;
        LocalDate defaultStart = YearMonth.from(today).atDay(1);

        LocalDate parsedStart = parseDate(dateDebut, defaultStart);
        LocalDate parsedEnd = parseDate(dateFin, defaultEnd);
        if (parsedStart.isAfter(parsedEnd)) {
            LocalDate swap = parsedStart;
            parsedStart = parsedEnd;
            parsedEnd = swap;
        }

        Projet selectedProject = resolveSelectedProject(projets, projetId);
        List<MouvementProjet> mouvements = selectedProject == null
                ? List.of()
                : mouvementRepository.findByProjetIdAndDateOperationBetweenOrderByDateOperationAsc(
                selectedProject.getId(), parsedStart, parsedEnd);

        Map<LocalDate, BigDecimal> netParJour = new LinkedHashMap<>();
        Map<LocalDate, BigDecimal> brutBeneficeParJour = new LinkedHashMap<>();
        Map<LocalDate, BigDecimal> brutPerteParJour = new LinkedHashMap<>();
        for (LocalDate date = parsedStart; !date.isAfter(parsedEnd); date = date.plusDays(1)) {
            netParJour.put(date, BigDecimal.ZERO);
            brutBeneficeParJour.put(date, BigDecimal.ZERO);
            brutPerteParJour.put(date, BigDecimal.ZERO);
        }

        BigDecimal totalBenefice = BigDecimal.ZERO;
        BigDecimal totalPerte = BigDecimal.ZERO;

        for (MouvementProjet mouvement : mouvements) {
            BigDecimal montant = mouvement.getMontant() == null ? BigDecimal.ZERO : mouvement.getMontant();
            LocalDate date = mouvement.getDateOperation();
            if (!netParJour.containsKey(date)) {
                continue;
            }

            if (mouvement.getNature() == NatureMouvement.BENEFICE) {
                totalBenefice = totalBenefice.add(montant);
                brutBeneficeParJour.put(date, brutBeneficeParJour.get(date).add(montant));
                netParJour.put(date, netParJour.get(date).add(montant));
            } else {
                totalPerte = totalPerte.add(montant);
                brutPerteParJour.put(date, brutPerteParJour.get(date).add(montant));
                netParJour.put(date, netParJour.get(date).subtract(montant));
            }
        }

        List<String> labels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();
        List<String> colors = new ArrayList<>();

        for (Map.Entry<LocalDate, BigDecimal> entry : netParJour.entrySet()) {
            labels.add(entry.getKey().format(DateTimeFormatter.ofPattern("dd/MM")));
            values.add(entry.getValue());
            colors.add(entry.getValue().compareTo(BigDecimal.ZERO) >= 0 ? "#22c55e" : "#ef4444");
        }

        BigDecimal pieBenefice = totalBenefice.max(BigDecimal.ZERO);
        BigDecimal piePerte = totalPerte.max(BigDecimal.ZERO);

        model.addAttribute("username", username);
        model.addAttribute("projets", projets);
        model.addAttribute("selectedProject", selectedProject);
        model.addAttribute("selectedProjectId", selectedProject == null ? null : selectedProject.getId());
        model.addAttribute("dateDebut", parsedStart.format(FORM_DATE));
        model.addAttribute("dateFin", parsedEnd.format(FORM_DATE));
        model.addAttribute("totalBenefice", totalBenefice);
        model.addAttribute("totalPerte", totalPerte);
        model.addAttribute("soldeNet", totalBenefice.subtract(totalPerte));
        model.addAttribute("nbMouvements", mouvements.size());
        model.addAttribute("dailyLabelsJson", toJson(labels));
        model.addAttribute("dailyValuesJson", toJson(values));
        model.addAttribute("dailyColorsJson", toJson(colors));
        model.addAttribute("pieLabelsJson", toJson(List.of("Perte", "Bénéfice")));
        model.addAttribute("pieValuesJson", toJson(List.of(piePerte, pieBenefice)));
        model.addAttribute("hasData", !projets.isEmpty());
        model.addAttribute("hasMovements", !mouvements.isEmpty());

        return "home";
    }

    private Projet resolveSelectedProject(List<Projet> projets, Long projetId) {
        if (projetId != null) {
            for (Projet projet : projets) {
                if (projet.getId().equals(projetId)) {
                    return projet;
                }
            }
        }
        return projets.isEmpty() ? null : projets.get(0);
    }

    private LocalDate parseDate(String value, LocalDate fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return LocalDate.parse(value, FORM_DATE);
    }

    private String toJson(Collection<?> values) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        for (Object value : values) {
            if (!first) {
                json.append(',');
            }
            json.append(toJsonValue(value));
            first = false;
        }
        json.append(']');
        return json.toString();
    }

    private String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        String text = value.toString()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
        return "\"" + text + "\"";
    }
}
