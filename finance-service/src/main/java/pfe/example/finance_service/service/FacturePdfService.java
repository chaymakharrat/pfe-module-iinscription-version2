package pfe.example.finance_service.security;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pfe.example.finance_service.client.NextcloudFinanceClient;
import pfe.example.finance_service.entities.Facture;
import pfe.example.finance_service.entities.FormulaireRemise;
import pfe.example.finance_service.repositories.FactureRepository;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturePdfService {

    private final FactureRepository factureRepository;
    private final NextcloudFinanceClient nextcloudClient;

    private static final String FACTURES_FOLDER = "factures";
    private static final DateTimeFormatter DATE_FR =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─── Couleurs ──────────────────────────────────────────────────────────
    private static final Color NAVY      = new Color(15,  23,  42);
    private static final Color BLUE      = new Color(37,  99, 235);
    private static final Color LIGHT_BG  = new Color(248, 250, 252);
    private static final Color BORDER    = new Color(226, 232, 240);
    private static final Color GREEN     = new Color(22, 163,  74);
    private static final Color GRAY_TXT  = new Color(100, 116, 139);
    private static final Color WHITE     = Color.WHITE;

    /**
     * Génère le PDF, l'upload dans Nextcloud et retourne le chemin.
     */
    public String generateAndStore(Long factureId) throws Exception {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée id=" + factureId));

        byte[] pdfBytes = buildPdf(facture);

        String remotePath = FACTURES_FOLDER + "/" + facture.getNumeroFacture() + ".pdf";
        nextcloudClient.createFolderIfNotExists(FACTURES_FOLDER);
        nextcloudClient.uploadFile(remotePath, pdfBytes, "application/pdf");

        log.info("✅ Facture PDF uploadée: {}", remotePath);
        return remotePath;
    }

    /**
     * Télécharge le PDF depuis Nextcloud (génère si absent).
     */
    public byte[] downloadPdf(Long factureId) throws Exception {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée id=" + factureId));

        String remotePath = FACTURES_FOLDER + "/" + facture.getNumeroFacture() + ".pdf";

        try {
            InputStream is = nextcloudClient.downloadFile(remotePath);
            return is.readAllBytes();
        } catch (Exception e) {
            log.warn("PDF absent de Nextcloud, génération à la volée...");
            byte[] pdfBytes = buildPdf(facture);
            nextcloudClient.createFolderIfNotExists(FACTURES_FOLDER);
            nextcloudClient.uploadFile(remotePath, pdfBytes, "application/pdf");
            return pdfBytes;
        }
    }

    // ─── Construction du PDF ───────────────────────────────────────────────
    private byte[] buildPdf(Facture facture) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);
        doc.open();

        // ── Fonts ──────────────────────────────────────────────────────────
        Font fontTitle   = new Font(Font.HELVETICA, 28, Font.BOLD,   NAVY);
        Font fontH2      = new Font(Font.HELVETICA, 13, Font.BOLD,   NAVY);
        Font fontLabel   = new Font(Font.HELVETICA,  9, Font.BOLD,   GRAY_TXT);
        Font fontValue   = new Font(Font.HELVETICA, 10, Font.NORMAL, NAVY);
        Font fontBold    = new Font(Font.HELVETICA, 10, Font.BOLD,   NAVY);
        Font fontSmall   = new Font(Font.HELVETICA,  8, Font.NORMAL, GRAY_TXT);
        Font fontWhite   = new Font(Font.HELVETICA, 10, Font.BOLD,   WHITE);
        Font fontGreen   = new Font(Font.HELVETICA, 10, Font.BOLD,   GREEN);
        Font fontTotal   = new Font(Font.HELVETICA, 14, Font.BOLD,   WHITE);

        // ══ HEADER ══════════════════════════════════════════════════════════
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{1.5f, 1f});
        header.setSpacingAfter(20);

        // Cellule gauche — nom société
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(12);
        leftCell.setBackgroundColor(NAVY);

        Paragraph brand = new Paragraph("ITECH", new Font(Font.HELVETICA, 22, Font.BOLD, WHITE));
        brand.setSpacingAfter(2);
        Paragraph brandSub = new Paragraph("University", new Font(Font.HELVETICA, 10, Font.NORMAL,
                new Color(148, 163, 184)));
        leftCell.addElement(brand);
        leftCell.addElement(brandSub);
        header.addCell(leftCell);

        // Cellule droite — titre FACTURE
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(12);
        rightCell.setBackgroundColor(BLUE);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph titreFacture = new Paragraph("FACTURE", fontTitle);
        titreFacture.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(titreFacture);

        Paragraph numFacture = new Paragraph("N° " + facture.getNumeroFacture(),
                new Font(Font.HELVETICA, 10, Font.BOLD, new Color(191, 219, 254)));
        numFacture.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(numFacture);
        header.addCell(rightCell);

        doc.add(header);

        // ══ META INFO ═══════════════════════════════════════════════════════
        PdfPTable meta = new PdfPTable(3);
        meta.setWidthPercentage(100);
        meta.setSpacingAfter(16);
        meta.setWidths(new float[]{1f, 1f, 1f});

        addMetaCell(meta, "DATE D'ÉMISSION",
                java.time.LocalDate.now().format(DATE_FR), fontLabel, fontBold);
        addMetaCell(meta, "RÉFÉRENCE",
                facture.getNumeroFacture(), fontLabel, fontBold);
        addMetaCell(meta, "STATUT",
                facture.getStatusPaiement().name(), fontLabel, fontBold);

        doc.add(meta);

        // ══ LIGNE SÉPARATRICE ════════════════════════════════════════════════
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        divider.setSpacingAfter(16);
        PdfPCell divCell = new PdfPCell();
        divCell.setFixedHeight(3f);
        divCell.setBackgroundColor(BLUE);
        divCell.setBorder(Rectangle.NO_BORDER);
        divider.addCell(divCell);
        doc.add(divider);

        // ══ TABLEAU ARTICLES ════════════════════════════════════════════════
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 1f, 1.5f});
        table.setSpacingAfter(0);

        // En-têtes colonnes
        addTableHeader(table, "DESCRIPTION",   fontWhite);
        addTableHeader(table, "QTÉ",           fontWhite);
        addTableHeader(table, "MONTANT",        fontWhite);

        // Ligne frais scolarité
        addTableRow(table, "Frais de scolarité", fontBold, fontValue, false);
        addTableRowCenter(table, "1", fontValue, false);
        addTableRowRight(table,
                String.format("%.2f TND", facture.getMontantBrut()), fontBold, false);

        // Lignes remises
        List<FormulaireRemise> remises = facture.getRemisesAppliquees();
        boolean alt = true;
        for (FormulaireRemise r : remises) {
            double montantRemise = facture.getMontantBrut() * r.getPourcentage() / 100.0;
            addTableRow(table,
                    "✨ Remise — " + r.getMotif() + " (" + r.getPourcentage() + "%)",
                    new Font(Font.HELVETICA, 10, Font.ITALIC, GREEN), fontValue, alt);
            addTableRowCenter(table, "1", fontValue, alt);
            addTableRowRight(table,
                    String.format("-%.2f TND", montantRemise), fontGreen, alt);
            alt = !alt;
        }

        doc.add(table);

        // ══ TOTAL ════════════════════════════════════════════════════════════
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(50);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.setSpacingBefore(0);
        totalTable.setSpacingAfter(20);
        totalTable.setWidths(new float[]{1.5f, 1f});

        // Sous-total brut
        addSummaryRow(totalTable, "Sous-total brut",
                String.format("%.2f TND", facture.getMontantBrut()),
                fontLabel, fontValue, LIGHT_BG);

        // Remise totale
        if (facture.getMontantBrut() > facture.getMontantTotal()) {
            double remiseTotale = facture.getMontantBrut() - facture.getMontantTotal();
            addSummaryRow(totalTable, "Réductions",
                    String.format("-%.2f TND", remiseTotale),
                    new Font(Font.HELVETICA, 9, Font.BOLD, GREEN),
                    new Font(Font.HELVETICA, 10, Font.BOLD, GREEN),
                    new Color(240, 253, 244));
        }

        // Total net — fond bleu
        PdfPCell totalLabelCell = new PdfPCell(
                new Phrase("TOTAL NET À PAYER", fontTotal));
        totalLabelCell.setBackgroundColor(BLUE);
        totalLabelCell.setBorder(Rectangle.NO_BORDER);
        totalLabelCell.setPadding(10);
        totalTable.addCell(totalLabelCell);

        PdfPCell totalValCell = new PdfPCell(
                new Phrase(String.format("%.2f TND", facture.getMontantTotal()), fontTotal));
        totalValCell.setBackgroundColor(BLUE);
        totalValCell.setBorder(Rectangle.NO_BORDER);
        totalValCell.setPadding(10);
        totalValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.addCell(totalValCell);

        doc.add(totalTable);

        // ══ PAIEMENTS ════════════════════════════════════════════════════════
        if (facture.getEcheances() != null && !facture.getEcheances().isEmpty()) {
            doc.add(new Paragraph("Calendrier de paiement",
                    new Font(Font.HELVETICA, 11, Font.BOLD, NAVY)));
            doc.add(new Paragraph(" "));

            PdfPTable echTable = new PdfPTable(4);
            echTable.setWidthPercentage(100);
            echTable.setSpacingAfter(20);
            echTable.setWidths(new float[]{0.5f, 1f, 1f, 1f});

            addTableHeader(echTable, "N°",           fontWhite);
            addTableHeader(echTable, "DATE LIMITE",  fontWhite);
            addTableHeader(echTable, "MONTANT",      fontWhite);
            addTableHeader(echTable, "STATUT",       fontWhite);

            boolean altEch = false;
            for (var ech : facture.getEcheances()) {
                Color bg = altEch ? LIGHT_BG : WHITE;
                addTableRowCenter(echTable, String.valueOf(ech.getNumeroOrdre()), fontValue, altEch);
                addTableRow(echTable,
                        ech.getDateEcheance() != null ? ech.getDateEcheance().format(DATE_FR) : "—",
                        fontValue, fontValue, altEch);
                addTableRowRight(echTable,
                        String.format("%.2f TND", ech.getMontantAPayer()), fontBold, altEch);
                String statut = "PAYE".equals(ech.getStatut()) ? "✓ Payée" : "⏳ En attente";
                Font statusFont = "PAYE".equals(ech.getStatut())
                        ? new Font(Font.HELVETICA, 9, Font.BOLD, GREEN)
                        : new Font(Font.HELVETICA, 9, Font.BOLD, BLUE);
                PdfPCell sc = new PdfPCell(new Phrase(statut, statusFont));
                sc.setBackgroundColor(bg);
                sc.setBorder(Rectangle.NO_BORDER);
                sc.setBorderWidthBottom(0.5f);
                sc.setBorderColorBottom(BORDER);
                sc.setPadding(8);
                sc.setHorizontalAlignment(Element.ALIGN_CENTER);
                echTable.addCell(sc);
                altEch = !altEch;
            }
            doc.add(echTable);
        }

        // ══ FOOTER ══════════════════════════════════════════════════════════
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);
        footer.setSpacingBefore(10);
        PdfPCell footerCell = new PdfPCell(
                new Phrase("ITECH University · Service Finance · finance@itech-university.tn",
                        fontSmall));
        footerCell.setBorder(Rectangle.TOP);
        footerCell.setBorderColorTop(BORDER);
        footerCell.setPaddingTop(8);
        footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footer.addCell(footerCell);
        doc.add(footer);

        doc.close();
        return baos.toByteArray();
    }

    // ─── Helpers ───────────────────────────────────────────────────────────
    private void addMetaCell(PdfPTable t, String label, String value,
                             Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(LIGHT_BG);
        cell.setPadding(10);
        cell.addElement(new Paragraph(label, labelFont));
        cell.addElement(new Paragraph(value, valueFont));
        t.addCell(cell);
    }

    private void addTableHeader(PdfPTable t, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(NAVY);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(10);
        t.addCell(cell);
    }

    private void addTableRow(PdfPTable t, String text, Font font,
                             Font unused, boolean alt) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(alt ? LIGHT_BG : WHITE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderWidthBottom(0.5f);
        cell.setBorderColorBottom(BORDER);
        cell.setPadding(9);
        t.addCell(cell);
    }

    private void addTableRowCenter(PdfPTable t, String text, Font font, boolean alt) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(alt ? LIGHT_BG : WHITE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderWidthBottom(0.5f);
        cell.setBorderColorBottom(BORDER);
        cell.setPadding(9);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        t.addCell(cell);
    }

    private void addTableRowRight(PdfPTable t, String text, Font font, boolean alt) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(alt ? LIGHT_BG : WHITE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderWidthBottom(0.5f);
        cell.setBorderColorBottom(BORDER);
        cell.setPadding(9);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(cell);
    }

    private void addSummaryRow(PdfPTable t, String label, String value,
                               Font labelFont, Font valueFont, Color bg) {
        PdfPCell lc = new PdfPCell(new Phrase(label, labelFont));
        lc.setBackgroundColor(bg);
        lc.setBorder(Rectangle.NO_BORDER);
        lc.setPadding(8);
        t.addCell(lc);

        PdfPCell vc = new PdfPCell(new Phrase(value, valueFont));
        vc.setBackgroundColor(bg);
        vc.setBorder(Rectangle.NO_BORDER);
        vc.setPadding(8);
        vc.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(vc);
    }
}