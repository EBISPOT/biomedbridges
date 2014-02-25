// Script for inserting header into web pages
// Must wrap Menu titles in div to support tool tip
// Cannot apply class-based styling to <ul> elements ... I don't know why! Hence do it by id-based styling instead


function addheader(e)
{
    var header =

    "<div id=\"logo-container\">" +
        "<a href=\"./index.html\"><img style=\"padding-left:10px\" src=\"./images/BioToolsLogo.png\" alt=\"Tools and Data Services Registry\"></a>" +
        "<a href=\"http://www.elixir-europe.org/\"><img style=\"padding-left:10px\" src=\"./images/ElixirLogo.png\" alt=\"ELIXIR\"></a>" +
    "<a href=\"http://www.biomedbridges.eu/\"><img  style=\"padding-left:10px\" src=\"./images/BMBLogo.jpg\" alt=\"BioMedBridges\"></a>" +
    "</div>" +

    "<div id=\"mainmenu-container\">" +
    "<div id=\"mainmenu\" >" +
    "<ul>" +

    "<div><li><a class=\'mainmenu-text\'  href=\"index.html\">Home</a></li></div>" +

        "<div><li><span class=\'mainmenu-text\' id=\"tipMenuAbout\">About</span>" +
        "<ul id=\"menu-about\" class=\'megamenu-ul\'>" +
        "<li><a href=\'about.html\'>About</a></li>" +
        "<li><a href=\'contribute.html\'>Contribute</a></li>" +
        "<li><a href=\'events.html\'>Events</a></li>" +
        "<li><a href=\'contactus.html\'>Contact Us</a></li>" +
        "</ul>" +
        "</li></div>" +

    "<div><li><span class=\'mainmenu-text\' id=\"tipMenuPartners\">Partners</span>" +
    "<ul id=\"menu-partners\" class=\"megamenu-ul\">" +
    "<li>BioMedBridges Partners" +
    "<ul id=\"menu-biomedbridges-partners\" >" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/embl\">European Molecular Biology Laboratory</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/uoxf\">University of Oxford</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/ki\">Karolinska Institutet</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/stfc\">Science and Technology Facilities Council</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/udus\">Heinrich Heine Universität Düsseldorf</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/fvb\">Leibniz-Institut für Molekulare Pharmakologie</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/tum-med\">Technische Universität München</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/szn\">Stazione Zoologica Anton Dohrn</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/erasmusmc\">Erasmus University Medical Center Rotterdam</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/tmf\">Technologie- und Methodenplattform für die vernetzte medizinische Forschung e.V.</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/hmgu\">Helmholtz Zentrum München</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/mug\">Medizinische Universität Graz</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/vumc\">Stichting VU-VUmc</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/inserm\">Institut national de la santé et de la recherche médicale</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/ucph\">University of Copenhagen</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/uh\">University of Helsinki, Institute for Molecular Medicine Finland</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/egi\">European Grid Infrastructure</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/csc\">CSC - IT Center for Science Ltd.</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/umcg\">University Medical Center Groningen</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/cirmmp\">Consorzio Interuniversitario di Risonanze Magnetiche di Metalloproteine</a></li>" +
    "<li><a href=\"http://www.biomedbridges.eu/partners/dante\">Delivery of Advanced Network Technology to Europe</a></li>" +
    "</ul>" +
    "</li>" +
    "</ul>" +
    "</li></div>" +

    "</ul>" +
    "</div>" +
    "</div>";


    e.append(header);
}