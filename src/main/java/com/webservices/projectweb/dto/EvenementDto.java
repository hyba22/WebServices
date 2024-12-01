package com.webservices.projectweb.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
public class EvenementDto {
    @NotEmpty(message = "Titre est obligatoire")
    private String titre;

    @NotEmpty(message = "Description est obligatoire")
    private String description;

    @NotNull(message = "Date est obligatoire")
    private Date date;

    @NotEmpty(message = "Localisation est obligatoire")
    private String localisation;

    @NotEmpty(message = "Categorie est obligatoire")
    private String categorie;

    @NotEmpty(message = "Etat est obligatoire")
    private String etat;
}
