// Script for inserting footer into web pages


function addfooter(e)
{
    var footer =
        "<a class=\"footer-text\" title=\"Terms of Use\" href=\"http://XXX/terms-of-use\"> Terms of Use</a>" +
            "<a class=\"footer-text\" title=\"Privacy Policy\" href=\"http://XXX/privacy\">Privacy Policy</a>" +
            "<a class=\"footer-text\" title=\"Follow Us\" href=\"http://XXX/follow\">Follow Us</a>" +
            "<a class=\"footer-text\" title=\"Contact Us\" href=\"http://XXX/contact-us\">Contact Us</a>" +
            "<a> <img class=\"footer-icon\" alt=\"Facebook Page\" title=\"Facebook Page\" src=\"./images/facebook.png\" /></a>" +
            "<a> <img class=\"footer-icon\" alt=\"Updates on Twitter\" title=\"Updates on Twitter\" src=\"./images/twitter.png\" /></a>" +
            "<a> <img class=\"footer-icon\" alt=\"RSS Feeds\" title=\"RSS Feeds\" src=\"./images/rss.png\" /></a>" +
            "<a> <img class=\"footer-icon\" alt=\"Updates on Google Plus\" title=\"Updates on Google Plus\" src=\"./images/googleplusicon.png\" /></a>";

    e.append(footer);
}
