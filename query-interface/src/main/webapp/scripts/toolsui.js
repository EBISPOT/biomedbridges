// Main javascript functions

function init()
{
$(document).ready(function()
    {
    init_headerfooter();
    });

    create_gridmenu();
//    create_tree();
    create_filters();
    create_grid();
}


/*
    Creates header (including general menu) and footer
    Calls:
    1) addheader to insert header
    2) addfooter to insert footer
 */
function init_headerfooter()
{
    // Add header and footer boilerplate HTML
    addheader($("#header"));
    addfooter($("#footer-container"));

    // Create main menu
    var theme = getDemoTheme();
    switch (theme)
    {
        case 'shinyblack':
            $($.find('.megamenu-ul li a:link')).css('color', '#fff');
            $($.find('.megamenu-ul li a:hover')).css('color', '#fff');
            $($.find('.megamenu-ul li a:visited')).css('color', '#fff');
            $($.find('.jqx-menuitem-header')).css('color', '#fff');
            break;
        default:
            $($.find('.megamenu-ul li a:link')).css('color', '#2d628a');
            $($.find('.megamenu-ul li a:hover')).css('color', '#2d628a');
            $($.find('.megamenu-ul li a:visited')).css('color', '#2d628a');
            $($.find('.jqx-menuitem-header')).css('color', '#2d628a');
            break;
    }
    $("#mainmenu").jqxMenu({autoOpen: false, autoCloseOnMouseLeave: true, showTopLevelArrows: true, theme: theme, rtl: true});
    $("#mainmenu").css('visibility', 'visible');

    $('#mainmenu').on('itemclick', function (event)
    {
        var item = $(event.target).text();

        if(item == "Excel")
            $("#jqxgrid").jqxGrid('exportdata', 'xls', 'jqxGrid');
        else if(item == "CSV")
            $("#jqxgrid").jqxGrid('exportdata', 'csv', 'jqxGrid');
        else if(item == "HTML")
            $("#jqxgrid").jqxGrid('exportdata', 'html', 'jqxGrid');
        else if(item == "XML")
            $("#jqxgrid").jqxGrid('exportdata', 'xml', 'jqxGrid');
        else if(item == "TSV")
            $("#jqxgrid").jqxGrid('exportdata', 'tsv', 'jqxGrid');
        else if(item == "JSON")
            $("#jqxgrid").jqxGrid('exportdata', 'json', 'jqxGrid');
    });
}


/*
    Creates grid menu
    Calls functions to
    1) create the checkboxes in the Show menu
    2) create the grid search button
 */
function create_gridmenu()
{
    // Create grid menu
    var theme = getDemoTheme();

    switch (theme)
    {
        case 'shinyblack':
            $($.find('.megamenu-ul li a:link')).css('color', '#fff');
            $($.find('.megamenu-ul li a:hover')).css('color', '#fff');
            $($.find('.megamenu-ul li a:visited')).css('color', '#fff');
            $($.find('.jqx-menuitem-header')).css('color', '#fff');
            break;
        default:
            $($.find('.megamenu-ul li a:link')).css('color', '#2d628a');
            $($.find('.megamenu-ul li a:hover')).css('color', '#2d628a');
            $($.find('.megamenu-ul li a:visited')).css('color', '#2d628a');
            $($.find('.jqx-menuitem-header')).css('color', '#2d628a');
            break;
    }
    $("#jqxMenuGrid").jqxMenu({ autoOpen: false, autoCloseOnMouseLeave: true, showTopLevelArrows: true, theme: theme});
    $("#jqxMenuGrid").css('visibility', 'visible');


    // Pick up "click" events
    $('#jqxMenuGrid').on('itemclick', function (event)
    {
        // get the clicked LI element.
        var element = event.args;

        switch(element.innerText)
        {
            case 'Clear all':
                $("#jqxgrid").jqxGrid('clearfilters');
                break;
            default:
                break;
        }
    });

    create_showMenu();
    create_searchBox();
//    create_searchButton();
    create_tooltips();
}



/*
    Create Show menu item
 */
function create_showMenu()
{
    var theme = getDemoTheme();

    // "Summary" checkboxes
//        $("#showName").jqxCheckBox({ checked: true, theme: theme });
//    $("#showHomepage").jqxCheckBox({ checked: false, theme: theme });
//    $("#showEntryID").jqxCheckBox({ checked: false, theme: theme });
    $("#showType").jqxCheckBox({ checked: true, theme: theme });
    $("#showCollection").jqxCheckBox({ checked: false, theme: theme });
//    $("#showUses").jqxCheckBox({ checked: false, theme: theme });
    $("#showDescription").jqxCheckBox({ checked: true, theme: theme });
    $("#showTopics").jqxCheckBox({ checked: true, theme: theme });
    $("#showTags").jqxCheckBox({ checked: false, theme: theme });
    $("#showVersion").jqxCheckBox({ checked: false, theme: theme });
    $("#showDownload").jqxCheckBox({ checked: false, theme: theme });

    // "Status" checkboxes
    $("#showMaturity").jqxCheckBox({ checked: false, theme: theme });
//    $("#showAvailability").jqxCheckBox({ checked: false, theme: theme });
//    $("#showDowntime").jqxCheckBox({ checked: false, theme: theme });

    // "Operations" checkboxes
    $("#showFunctions").jqxCheckBox({ checked: true, theme: theme });
//    $("#showFunctionDescription").jqxCheckBox({ checked: false, theme: theme });
//    $("#showFunctionHandle").jqxCheckBox({ checked: false, theme: theme });
    $("#showInputTypes").jqxCheckBox({ checked: true, theme: theme });
    $("#showInputFormats").jqxCheckBox({ checked: false, theme: theme });
//    $("#showInputHandle").jqxCheckBox({ checked: false, theme: theme });
    $("#showOutputTypes").jqxCheckBox({ checked: true, theme: theme });
    $("#showOutputFormats").jqxCheckBox({ checked: false, theme: theme });
//    $("#showOutputHandle").jqxCheckBox({ checked: false, theme: theme });

    // "Interfaces" checkboxes
    $("#showInterfacesCommandLine").jqxCheckBox({ checked: false, theme: theme });
    $("#showInterfacesWebUI").jqxCheckBox({ checked: true, theme: theme });
    $("#showInterfacesDesktopGUI").jqxCheckBox({ checked: false, theme: theme });
    $("#showInterfacesRESTAPI").jqxCheckBox({ checked: true, theme: theme });
    $("#showInterfacesSOAPAPI").jqxCheckBox({ checked: false, theme: theme });
    $("#showInterfacesVMI").jqxCheckBox({ checked: false, theme: theme });

    // "Platforms" checkboxes
    $("#showPlatformsWindows").jqxCheckBox({ checked: false, theme: theme });
    $("#showPlatformsLinux").jqxCheckBox({ checked: false, theme: theme });
    $("#showPlatformsOSX").jqxCheckBox({ checked: false, theme: theme });

    // "Usage" checkboxes
    $("#showLanguages").jqxCheckBox({ checked: false, theme: theme });

    // "Docs" checkboxes
    $("#showDocsHome").jqxCheckBox({ checked: false, theme: theme });
    $("#showDocsCommandLine").jqxCheckBox({ checked: false, theme: theme });
    $("#showDocsREST").jqxCheckBox({ checked: false, theme: theme });
    $("#showDocsSOAP").jqxCheckBox({ checked: false, theme: theme });
    $("#showDocsSPARQL").jqxCheckBox({ checked: false, theme: theme });
    $("#showWSDL").jqxCheckBox({ checked: false, theme: theme });

    // "Contacts" checkboxes
    $("#showHelpdesk").jqxCheckBox({ checked: false, theme: theme });
//    $("#showContactPage").jqxCheckBox({ checked: false, theme: theme });
    $("#showContactName").jqxCheckBox({ checked: false, theme: theme });
//    $("#showContact").jqxCheckBox({ checked: false, theme: theme });
//    $("#showContactID").jqxCheckBox({ checked: false, theme: theme });
    $("#showContactTel").jqxCheckBox({ checked: false, theme: theme });

    // "Credits" checkboxes
    $("#showDeveloper").jqxCheckBox({ checked: false, theme: theme });
    $("#showDeveloperInterface").jqxCheckBox({ checked: false, theme: theme });
    $("#showContributors").jqxCheckBox({ checked: false, theme: theme });
    $("#showInstitutions").jqxCheckBox({ checked: false, theme: theme });
    $("#showInfrastructures").jqxCheckBox({ checked: false, theme: theme });
    $("#showFunding").jqxCheckBox({ checked: false, theme: theme });
    $("#showWorkPackages").jqxCheckBox({ checked: false, theme: theme });

    // "Literature" checkboxes
    $("#showPublicationsPrimary").jqxCheckBox({ checked: false, theme: theme });
    $("#showPublicationsOther").jqxCheckBox({ checked: false, theme: theme });
    $("#showCitations").jqxCheckBox({ checked: false, theme: theme });

    // "Restrictions" checkboxes
    $("#showCost").jqxCheckBox({ checked: false, theme: theme });
    $("#showLicense").jqxCheckBox({ checked: false, theme: theme });
    $("#showTermsOfUse").jqxCheckBox({ checked: false, theme: theme });

    // "Registration" checkboxes
//    $("#showRegistrantName").jqxCheckBox({ checked: false, theme: theme });
//    $("#showRegistrantEmail").jqxCheckBox({ checked: false, theme: theme });
//    $("#showAdditionDate").jqxCheckBox({ checked: false, theme: theme });
//    $("#showLastUpdate").jqxCheckBox({ checked: false, theme: theme });




// "Summary" checkboxes
//        addColumnToggling($("#showName"), "Name");
//    addColumnToggling($("#showHomepage"), "Homepage");
    //    addColumnToggling($("#showEntryID"), "EntryID");

    addColumnToggling($("#showType"), "Type");
    addColumnToggling($("#showCollection"), "Collection");
//    addColumnToggling($("#showUses"), "Uses");
    addColumnToggling($("#showDescription"), "Description");
    addColumnToggling($("#showTopics"), "Topics");
    addColumnToggling($("#showTags"), "Tags");
    addColumnToggling($("#showVersion"), "Version");
    addColumnToggling($("#showDownload"), "Download");

// "Status" checkboxes
    addColumnToggling($("#showMaturity"), "Maturity");
//    addColumnToggling($("#showAvailability"), "Availability");
//    addColumnToggling($("#showDowntime"), "Downtime");

// "Operations" checkboxes
    addColumnToggling($("#showFunctions"), "Functions");
//    addColumnToggling($("#showFunctionDescription"), "FunctionDescription");
//    addColumnToggling($("#showFunctionHandle"), "FunctionHandle");
    addColumnToggling($("#showInputTypes"), "InputTypes");
    addColumnToggling($("#showInputFormats"), "InputFormats");
//    addColumnToggling($("#showInputHandle"), "InputHandle");
    addColumnToggling($("#showOutputTypes"), "OutputTypes");
    addColumnToggling($("#showOutputFormats"), "OutputFormats");
//    addColumnToggling($("#showOutputHandle"), "OutputHandle");

// "Interfaces" checkboxes
    addColumnToggling($("#showInterfacesCommandLine"), "InterfacesCommandLine");
    addColumnToggling($("#showInterfacesWebUI"), "InterfacesWebUI");
    addColumnToggling($("#showInterfacesDesktopGUI"), "InterfacesDesktopGUI");
    addColumnToggling($("#showInterfacesRESTAPI"), "InterfacesRESTAPI");
    addColumnToggling($("#showInterfacesSOAPAPI"), "InterfacesSOAPAPI");
    addColumnToggling($("#showInterfacesVMI"), "InterfacesVMI");

    // "Platforms" checkboxes
    addColumnToggling($("#showPlatformsWindows"), "PlatformsWindows");
    addColumnToggling($("#showPlatformsLinux"), "PlatformsLinux");
    addColumnToggling($("#showPlatformsOSX"), "PlatformsOSX");

// "Usage" checkboxes
    addColumnToggling($("#showLanguages"), "Languages");

// "DocsUsage" checkboxes
    addColumnToggling($("#showDocsHome"), "DocsHome");
    addColumnToggling($("#showDocsCommandLine"), "DocsCommandLine");
    addColumnToggling($("#showDocsREST"), "DocsREST");
    addColumnToggling($("#showDocsSOAP"), "DocsSOAP");
    addColumnToggling($("#showDocsSPARQL"), "DocsSPARQL");
    addColumnToggling($("#showWSDL"), "WSDL");

// "Contacts" checkboxes
    addColumnToggling($("#showHelpdesk"), "Helpdesk");
//    addColumnToggling($("#showContactPage"), "ContactPage");
    addColumnToggling($("#showContactName"), "ContactName");
//    addColumnToggling($("#showContact"), "Contact");
//    addColumnToggling($("#showContactID"), "ContactID");
    addColumnToggling($("#showContactTel"), "ContactTel");

// "Credits" checkboxes
    addColumnToggling($("#showDeveloper"), "Developer");
    addColumnToggling($("#showDeveloperInterface"), "DeveloperInterface");
    addColumnToggling($("#showContributors"), "Contributors");
    addColumnToggling($("#showInstitutions"), "Institutions");
    addColumnToggling($("#showInfrastructures"), "Infrastructures");
    addColumnToggling($("#showFunding"), "Funding");
    addColumnToggling($("#showWorkPackages"), "WorkPackages");

// "Literature" checkboxes
    addColumnToggling($("#showPublicationsPrimary"), "PublicationsPrimary");
    addColumnToggling($("#showPublicationsOther"), "PublicationsOther");
    addColumnToggling($("#showCitations"),    "Citations");

// "Restrictions" checkboxes
    addColumnToggling($("#showCost"), "Cost");
    addColumnToggling($("#showLicense"), "License");
    addColumnToggling($("#showTermsOfUse"), "TermsOfUse");

// "Registration" checkboxes
//    addColumnToggling($("#showRegistrantName"), "RegistrantName");
//    addColumnToggling($("#showRegistrantEmail"), "RegistrantEmail");
//    addColumnToggling($("#showAdditionDate"), "AdditionDate");
//    addColumnToggling($("#showLastUpdate"), "LastUpdate");
}


/*
    Binds functions of checkboxes in Show menu
 */
function addColumnToggling(element, colName)
{
    element.bind('change', function (event)
    {
        if (event.args.checked)
        {$("#jqxgrid").jqxGrid('showcolumn', colName);}
        else
        {$("#jqxgrid").jqxGrid('hidecolumn', colName);}
    });
}


/*
 Create search box
 */
function create_searchBox()
{
    var theme = getDemoTheme();

//     var url = "SearchTerms.xml";
     var url = "data/registrySearchTerms.xml";


    // prepare the data
//    var source =
//    {
//        datatype: "xml",
//        datafields:
//        [
//            { name: 'Name' },
//            { name: 'Type' },
//            { name: 'SearchTerms' }
//        ],
//        root: "Tools",
//        record: "Tool",
//        id: 'Toolid',
//        url: url
//    };




     var source =
     {
         datatype: "xml",
         datafields:
             [
                 { name: 'TermName'}
             ],
        root: "Terms",
        record: "Term",
         url: url
     };


    var countries = new Array("Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burma", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo, Democratic Republic", "Congo, Republic of the", "Costa Rica", "Cote d'Ivoire", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Greenland", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, North", "Korea, South", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Mongolia", "Morocco", "Monaco", "Mozambique", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Norway", "Oman", "Pakistan", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Samoa", "San Marino", " Sao Tome", "Saudi Arabia", "Senegal", "Serbia and Montenegro", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe");

    var dataAdapter = new $.jqx.dataAdapter(source);


   // Create a jqxInput
    $("#SearchBox").jqxInput({
            source: dataAdapter,
            placeHolder: "Filter terms",
            displayMember: "TermName",
            valueMember: "TermName",
            width: 200,
            height: 25,
            searchMode: "containsignorecase",
        items: 20,
            theme: theme });

     $("#search").click(function ()
     {
         do_search();
     });
     if (theme == "black" || theme == "darkblue" || theme == "shinyblack")
     {
         $("img")[0].src = "jqwidgets/images/search_white_lg.png"
     }



    $("#SearchBox").on('select', function (event)
    {
        if (event.args)
        {
            var item = event.args.item;
            if (item)
            {
                var valueelement = $("<div></div>");
                valueelement.text("Value: " + item.value);
                var labelelement = $("<div></div>");
                labelelement.text("Label: " + item.label);

//                alert("Value:" +  item.value + "\n" +
//                    "Label:" + item.label + "\n");


//                $("#selectionlog").children().remove();
//                $("#selectionlog").append(labelelement);
//                $("#selectionlog").append(valueelement);
            }
//            else
//                alert("Fekin null_1");
        }
//            else
//        alert("Fekin null_2");

        do_search();

    });

     $("#SearchBox").on('change', function ()
     {
     do_search();
     });

     $("#SearchBox").on('select', function ()
     {
         do_search();
     });

 }



/*
    Create search button
 */
function create_searchButton_old()
{
    var theme = getDemoTheme();

    // Create button
    $("#SearchButton").jqxButton({theme: theme });

    // Define function on button click
    $('#SearchButton').click(function ()
    {
        do_search();
//        // clear existing grid filters
//        $("#jqxgrid").jqxGrid('clearfilters');
//
//        var datafield   = 'Description';
//        var filtervalue = $("#SearchBox").val();
//        var filtergroup = new $.jqx.filter();
//        var filter_or_operator = 1;
//        var filtercondition = 'contains';
//        var filter = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
//        filtergroup.addfilter(filter_or_operator, filter);
//        $("#jqxgrid").jqxGrid('addfilter', datafield, filtergroup);
//
//        // apply the new filters
//        $("#jqxgrid").jqxGrid('applyfilters');
    });
}


/* Do search: set filters using the term from the SearchBox */
function do_search()
{
    // clear existing grid filters
    $("#jqxgrid").jqxGrid('clearfilters');

//    var datafield   = 'Description';
    var datafield   = 'SearchTerms';
    var filtervalue = $("#SearchBox").val();
    var filtergroup = new $.jqx.filter();
    var filter_or_operator = 1;
    var filtercondition = 'contains';
    var filter = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
    filtergroup.addfilter(filter_or_operator, filter);
    $("#jqxgrid").jqxGrid('addfilter', datafield, filtergroup);

    // apply the new filters
    $("#jqxgrid").jqxGrid('applyfilters');


    /* This is a bit of a hack (duplicates code elsewhere) and needs consolidating */
    if(filtervalue == '')
    {
        $('#clearfilter').val("(filters off)");
        $('#clearfilter').css('color', "#f8fcfe");
        $('#clearfilter').css('backgroundColor', "#e8e8e8");

    }
}


/*
    Create filters
 */
function create_filters()
{
    var theme = getDemoTheme();

}




/*
 Create faceting tree
 */
function create_tree()
{
    var theme = getDemoTheme();

    // prepare the data
    var source =
    {
        datatype: "xml",
        datafields: [
            //                { name: 'AttributeGroup', map: 'm\\:properties>d\\:AttributeGroup' },
            //                { name: 'AttributeName', map: 'm\\:properties>d\\:AttributeName' },
            //                { name: 'AttributeDesc', map: 'm\\:properties>d\\:AttributeDesc' },
            //                { name: 'AttributeTerm', map: 'm\\:properties>d\\:AttributeTerm' }

            { name: 'AttributeName', map: 'm\\:properties>d\\:AttributeName' },
            { name: 'AttributeTerm', map: 'm\\:properties>d\\:AttributeTerm' }
        ],
        root: "entry",
        record: "content",
        id: 'm\\:properties>d\\:AttributeID',
        url: "facets.xml",
        async: false
    };




    // create data adapter.
    var dataAdapter = new $.jqx.dataAdapter(source);

    // perform Data Binding.
    dataAdapter.dataBind();
    /* Gets the array of the loaded data records and groups them.
     Param1 is an array of grouping fields (add more elements here to get more tree levels)
     Param2 is the sub items collection name (unused)
     Param3 is the group's name.
     Param4 specifies the mapping between the Data Source fields and custom data fields.
     */


//        var records = dataAdapter.getGroupedRecords(['AttributeGroup', 'AttributeName'], 'items', 'label', [{ name: 'uid', map: 'value' }, { name: 'AttributeTerm', map: 'label'}], 'row', 'value');


    var records = dataAdapter.getGroupedRecords(
        ['AttributeName'],
        'items',
        'label',
        [{ name: 'AttributeTerm', map: 'label'}]);


    /*
     var records = dataAdapter.getGroupedRecords(
     ['AttributeName', 'AttributeTerm'], 'items', 'label',
     [{ name: 'uid', map: 'value' }, { name: 'AttributeTerm', map: 'label'}],
     'row', 'value');
     */


    $('#jqxtree').jqxTree({width: "100%", height: "100%", source: records, hasThreeStates: true, checkboxes: true,  theme: theme });
//        $('#jqxtree').jqxTree('checkAll');

    $("#jqxtree").bind('checkChange', function (event)
    {
        var args = event.args;
        var element = args.element;
        var checked = args.checked;

        var item   = $('#jqxtree').jqxTree('getItem', element);
        var parent = $('#jqxtree').jqxTree('getItem', item.parentElement);

        // Exclude top-level nodes
        if(parent)
        {
            $('#jqxgrid').jqxGrid('removefilter', parent.label, true);

            var checkedItems = $('#jqxtree').jqxTree('getCheckedItems');

            var filtergroup = new $.jqx.filter();

            for (var i = 0; i < checkedItems.length; i++)
            {
                var node   = $('#jqxtree').jqxTree('getItem', checkedItems[i]);
                var nodeParent = $('#jqxtree').jqxTree('getItem', checkedItems[i].parentElement);

                if(nodeParent == parent)
                {
                    if(node.checked)
                    {
                        // Create new row filter
                        var filter = filtergroup.createfilter('stringfilter', node.label, 'EQUAL');

                        // 1st arg is the filter operator: 0 for "and", 1 for "or"
                        filtergroup.addfilter(1, filter);

//                                alert("Adding filter" + " " + node.label);


                        // add and apply the filter
                        // parent.label is the datafield (column) to which the filter will be applied, 'true' means apply the filter
                        if(i)
                            $('#jqxgrid').jqxGrid('addfilter', parent.label, filtergroup, true);

                        $('#jqxgrid').jqxGrid('refreshfilterrow');

//                    alert(item.label + " " + parent.label);
                    }
                }
            }

//            $('#jqxgrid').jqxGrid('render');
        }
    });
}





/*
 Create grid
 */
function create_grid()
{
    var theme = getDemoTheme();

    var url = "data/tools.xml";

//    var url = "registryContent.xml";

    // prepare the data
    var source =
    {
        // Set to async to ensure grid is loaded so that filtering widget can be configured, see http://www.jqwidgets.com/community/topic/problem-with-filterbox-records/
//        async: false,
        datatype: "xml",
        displayfields:
            [
                { name: 'TypeURI' },
                { name: 'TopicsURI' },
                { name: 'TagsURI' },
                { name: 'MaturityURI' },
                { name: 'FunctionsURI' },
                { name: 'InputTypesURI' },
                { name: 'InputFormatsURI' },
                { name: 'InputHandleURI' },
                { name: 'OutputTypesURI' },
                { name: 'OutputFormatsURI' },
                { name: 'OutputHandleURI' },
                { name: 'InterfacesURI' },
                { name: 'PlatformsURI' },
                { name: 'LanguagesURI' },
                { name: 'CostURI' },
                { name: 'LicenseURI' }
            ],
        datafields: [
            { name: 'Name' },
//            { name: 'EntryID' },
            { name: 'Type' },
            { name: 'Collection' },
//            { name: 'Uses' },
            { name: 'Description' },
            { name: 'Topics' },
            { name: 'Tags' },
            { name: 'Version' },
            { name: 'Download' },

            { name: 'Maturity' },
//            { name: 'Availability', type: 'bool' },
//            { name: 'Downtime'},

            { name: 'Functions' },
//            { name: 'FunctionDescription' },
//            { name: 'FunctionHandle' },
            { name: 'InputTypes' },
//            { name: 'InputFormats', map: 'input>dataFormat' },
            { name: 'InputFormats' },
//            { name: 'InputHandle' },
            { name: 'OutputTypes' },
//            { name: 'OutputFormats', map: 'output>dataFormat'  },
            { name: 'OutputFormats'},
//            { name: 'OutputHandle' },

            { name: 'InterfacesCommandLine', type: 'bool'  },
            { name: 'InterfacesWebUI', type: 'bool'  },
            { name: 'InterfacesDesktopGUI', type: 'bool'  },
            { name: 'InterfacesRESTAPI', type: 'bool'  },
            { name: 'InterfacesSOAPAPI', type: 'bool'  },
            { name: 'InterfacesVMI', type: 'bool'  },

            { name: 'PlatformsWindows', type: 'bool' },
            { name: 'PlatformsLinux', type: 'bool' },
            { name: 'PlatformsOSX', type: 'bool' },

            { name: 'Languages' },

            { name: 'DocsHome' },
            { name: 'DocsCommandLine' },
            { name: 'DocsREST' },
            { name: 'DocsSOAP' },
            { name: 'DocsSPARQL' },
            { name: 'WSDL' },

            { name: 'Helpdesk' },
//            { name: 'ContactPage' },
            { name: 'ContactName' },
//            { name: 'Contact' },
//            { name: 'ContactID' },
            { name: 'ContactTel' },

            { name: 'Developer' },
            { name: 'DeveloperInterface' },
            { name: 'Contributors' },
            { name: 'Institutions' },
            { name: 'Infrastructures' },
            { name: 'Funding' },
            { name: 'WorkPackages' },

            { name: 'PublicationsPrimary' },
            { name: 'PublicationsOther' },
            { name: 'Citations', type: 'int'},

            { name: 'Cost' },
            { name: 'License' },
            { name: 'TermsOfUse' },

//            { name: 'RegistrantName' },
//            { name: 'RegistrantEmail' },
//            { name: 'AdditionDate' },
//            { name: 'LastUpdate' },

            { name: 'SearchTerms' }

        ],
        root: "Tools",
        record: "Tool",
        id: 'toolid',
        url: url
    };


    // Filter
    var addfilter = function ()
    {
        var filtergroup = new $.jqx.filter();
        var filter_or_operator = 1;
        var filtervalue = 'Beate';
        var filtercondition = 'contains';
        var filter1 = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
        filtervalue = 'Andrew';
        filtercondition = 'starts_with';
        var filter2 = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);

        filtergroup.addfilter(filter_or_operator, filter1);
        filtergroup.addfilter(filter_or_operator, filter2);
        // add the filters.
        $("#jqxgrid").jqxGrid('addfilter', 'Type', filtergroup);
        // apply the filters.
        $("#jqxgrid").jqxGrid('applyfilters');
    };

    var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties)
    {
        if (value < 20)
        {
            return '<span style="margin: 4px; float: ' + columnproperties.cellsalign + '; color: #ff0000;">' + value + '</span>';
        }
        else
        {
            return '<span style="margin: 4px; float: ' + columnproperties.cellsalign + '; color: #008000;">' + value + '</span>';
        }
    };
    var dataAdapter = new $.jqx.dataAdapter(source,
        {
            downloadComplete: function (data, status, xhr) {},
            loadComplete: function (data) {},
            loadError: function (xhr, status, error) {}
        });




    /*
     * Returns HTML to render a link (URL) for 'value' (which is a URL)
     * e.g. for this xml:
     *   <Contact>help@uniprot.org#mailto:help@uniprot.org</Contact>
     */
    var simplelinkrenderer = function (row, column, value)
    {
        if (value.indexOf('|') != -1)
        {
            value = value.substring(0, value.indexOf('|'));
        }
        var format = { target: '"_blank"' };

        var token = $.jqx.dataFormat.formatlink(value, format);

        // This harsh styling is copied from the standard cell renderer - so that we get nice spacing of cell contents for this custom renderer
        return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
            token +
            "</div>";


//        return $.jqx.dataFormat.formatlink(value, format);

//        return html;
    };



    /*
     * Returns HTML to render an icon depending on a cell value ('value')
     */
    var iconrenderer = function (row, column, value)
    {
        if(value.length == 0)
            return;

        if (value == "Alpha ")
        {
            return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
                "<img src=\"images/alpha.jpg\" />" +
                "</div>";
        }
        else if (value == "Beta ")
        {
            return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
                "<img src=\"images/beta.jpg\" />" +
                "</div>";
        }
        if (value == "Production ")
        {
            return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
                "<img src=\"images/production.jpg\" />" +
                "</div>";
        }
        if ((value == "Free ") | (value=="Free for academic use ") | (value=="Free for non profit "))
        {
            return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
                "<img src=\"images/free.jpg\" />" +
                "</div>";
        }
        if (value == "Not free ")
        {
            return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
                "<img src=\"images/notfree.jpg\" />" +
                "</div>";
        }
    }


    /*
     * Returns HTML to render a link (as an icon) for 'value' (which is a URL)
     * e.g. for this xml:
     *   <Helpdesk>help@uniprot.org#mailto:help@uniprot.org</Helpdesk>
     */
    var iconlinkrenderer = function (row, column, value)
    {
        if(value.length == 0)
        return;

        if (value.indexOf('|') != -1)
        {
            value = value.substring(0, value.indexOf('|'));
        }

//        alert(column);

        if(column== 'TermsOfUse')
        {
        // This harsh styling is copied from the standard cell renderer - so that we get nice spacing of cell contents for this custom renderer
        return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
            "<a href='" + value + "'>" +
            "<img src=\"images/termsofuse.jpg\" />" +
            "</ a>" +
            "</div>";
        }
        else if(column== 'Helpdesk')
        {
            // This harsh styling is copied from the standard cell renderer - so that we get nice spacing of cell contents for this custom renderer
            return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
                "<a href='" + value + "'>" +
                "<img src=\"images/helpdesk.jpg\" />" +
                "</ a>" +
                "</div>";
        }
        else if(column== 'Download')
        {
            // This harsh styling is copied from the standard cell renderer - so that we get nice spacing of cell contents for this custom renderer
            return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
                "<a href='" + value + "'>" +
                "<img src=\"images/download.jpg\" />" +
                "</ a>" +
                "</div>";
        }
        else if( (column== 'DocsHome') | (column== 'DocsCommandLine') | (column== 'DocsREST') | (column== 'DocsSOAP') | (column== 'DocsSPARQL') | (column== 'WSDL'))
        {
            // This harsh styling is copied from the standard cell renderer - so that we get nice spacing of cell contents for this custom renderer
            return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
                "<a href='" + value + "'>" +
                "<img src=\"images/docs.jpg\" />" +
                "</ a>" +
                "</div>";
        }
        else if( (column== 'PublicationsPrimary') | (column== 'PublicationsOther'))
        {
            // This harsh styling is copied from the standard cell renderer - so that we get nice spacing of cell contents for this custom renderer
            return "<div style='overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: center; margin-right: 2px; margin-left: 4px; margin-top: 4px;'>" +
                "<a href='" + value + "'>" +
                "<img src=\"images/publication.jpg\" />" +
                "</ a>" +
                "</div>";
        }




            };



    /*
     * Returns HTML to render a link (name) for 'value' (which is a name and URL delimited by '|')
     * e.g. for this xml:
     *    <InputFormat>Textual format|http://edamontology.org/format_2330</InputFormat>
     */

    var linkrenderer = function (row, column, value)
    {
        if (value.indexOf('|') != -1)
        {
            var length = value.length;

            var linkText = value.substring(0, value.indexOf('|'));
            value        = value.substring(value.indexOf('|')+1, length);

            return "<a href='" + value + "' target='_blank'>" + linkText + "</a>";

        }
        else
        {
//            alert("No pipe - returning empty string")
            return "";
        }
    };


    /*
     columntype: 'textbox', filtertype: 'textbox', filtercondition: 'contains',
     filtertype: 'bool',
     filtertype: 'number',
     filtertype: 'checkedlist',
     */






    var initrowdetails = function (index, parentElement, gridElement, datarecord)
    {
        var tabsdiv = null;
        var information = null;
        var description = null;
        var credits = null;
        tabsdiv = $($(parentElement).children()[0]);
        if (tabsdiv != null)
        {
            // .information, .description, .credits and .title are defined in the grid "rowdetailstemplate"
            information = tabsdiv.find('.information');
            description = tabsdiv.find('.description');
            credits     = tabsdiv.find('.credits');
            var title = tabsdiv.find('.title');

            // Set title of 1st tab
//            title.text(datarecord.Name);

            // Define container for and columns in 'Summary' tab (rowdetails popup)
            var container = $('<div style="margin: 5px;"></div>')
            container.appendTo($(information));
            var photocolumn = $('<div style="float: left; width: 20%;"></div>');
            var leftcolumn = $('<div style="float: left; width: 40%;"></div>');
            var rightcolumn = $('<div style="float: left; width: 40%;"></div>');
            container.append(photocolumn);
            container.append(leftcolumn);
            container.append(rightcolumn);

            // Configure 'Summary' tab column 1 (photo)
            var photo = $("<div class='jqx-rc-all' style='margin: 10px;'> </div>");
            var image = $("<div style='margin-top: 10px;'></div>");
            var imgurl = 'images/BioToolsLogoSmall.png';
            var img = $('<img height="60" src="' + imgurl + '"/>');
            image.append(img);
            image.appendTo(photo);
            photocolumn.append(photo);

            // Configure 'Summary' tab column 2
            var name = "<div style='margin: 10px;'><b>Name:</b> " + datarecord.Name + "</div>";
            var collection = "<div style='margin: 10px;'><b>Collection:</b> " + datarecord.Collection + "</div>";
            var type = "<div style='margin: 10px;'><b>Type:</b> " + datarecord.Type + "</div>";
            var address = "<div style='margin: 10px;'><b>Address:</b> " + datarecord.Name + "</div>";
            $(leftcolumn).append(name);
            $(leftcolumn).append(collection);
            $(leftcolumn).append(type);
            $(leftcolumn).append(address);

            // Configure 'Summary' tab column 3
            var postalcode = "<div style='margin: 10px;'><b>Postal Code:</b> " + datarecord.Name + "</div>";
            var city = "<div style='margin: 10px;'><b>City:</b> " + datarecord.Name + "</div>";
            var phone = "<div style='margin: 10px;'><b>Phone:</b> " + datarecord.Name + "</div>";
            var hiredate = "<div style='margin: 10px;'><b>Hire Date:</b> " + datarecord.Name + "</div>";
            $(rightcolumn).append(postalcode);
            $(rightcolumn).append(city);
            $(rightcolumn).append(phone);
            $(rightcolumn).append(hiredate);

            // Configure 'Description' tab
            var descriptioncontainer = $('<div style="white-space: normal; margin: 5px;"><span>' + datarecord.Description + '</span></div>');
            $(description).append(descriptioncontainer);
            $(tabsdiv).jqxTabs({ width: 750, height: 170});


            // Define container for and columns in 'Summary' tab (rowdetails popup)
            var container = $('<div style="margin: 5px;"></div>')
            container.appendTo($(credits));
            var leftcolumn = $('<div style="float: left; width: 40%;"></div>');
            var rightcolumn = $('<div style="float: left; width: 40%;"></div>');
            container.append(leftcolumn);
            container.append(rightcolumn);

            // Configure 'Credits' tab column 1
            var developer        = "<div style='margin: 10px;'><b>Developer:</b> " + datarecord.Developer + "</div>";
            var interface        = "<div style='margin: 10px;'><b>Interface:</b> " + datarecord.DeveloperInterface + "</div>";
            var contributors     = "<div style='margin: 10px;'><b>Contributors:</b> " + datarecord.Contributors + "</div>";
            var institutions     = "<div style='margin: 10px;'><b>Institutions:</b> " + datarecord.Institutions + "</div>";
            $(leftcolumn).append(developer);
            $(leftcolumn).append(interface);
            $(leftcolumn).append(contributors);
            $(leftcolumn).append(institutions);

            // Configure 'Credits' tab column 2
            var infrastructures  = "<div style='margin: 10px;'><b>Infrastructures:</b> " + datarecord.Infrastructures + "</div>";
            var funding          = "<div style='margin: 10px;'><b>Funding:</b> " + datarecord.Funding + "</div>";
            var workpackages     = "<div style='margin: 10px;'><b>Work packages:</b> " + datarecord.WorkPackages + "</div>";

            $(rightcolumn).append(infrastructures);
            $(rightcolumn).append(funding);
            $(rightcolumn).append(workpackages);
        }
    }




        var columns = [
            // "Summary"
            { text: 'Name', datafield: 'Name', width: 100, rendered: tooltiprenderer, cellsrenderer: linkrenderer, pinned: true},
//            { text: 'Entry ID', datafield: 'EntryID', width: "auto", rendered: tooltiprenderer, hidden: true},
            { text: 'Type', datafield: 'Type', filtertype: 'checkedlist', width: 100, rendered: tooltiprenderer, hidden: false},

            { text: 'Collection', datafield: 'Collection', filtertype: 'checkedlist', width: "auto", rendered: tooltiprenderer},
//            { text: 'Uses', datafield: 'Uses', width: "auto", rendered: tooltiprenderer, hidden: true, filterable: false},
            { text: 'Description', datafield: 'Description', width: "auto", rendered: tooltiprenderer},
            { text: 'Topics', datafield: 'Topics', width: "auto", rendered: tooltiprenderer},
            { text: 'Tags', datafield: 'Tags', width: "auto", rendered: tooltiprenderer, hidden: true},
            { text: 'Version', datafield: 'Version', width: 75, rendered: tooltiprenderer, hidden: true},
            { text: 'Download', datafield: 'Download', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconlinkrenderer, filterable: false},


            // "Status"
            { text: 'Maturity', datafield: 'Maturity', filtertype: 'checkedlist', width: 75, rendered: tooltiprenderer, cellsrenderer: iconrenderer, hidden: true},
//            { text: 'Availability', columntype: 'checkbox', datafield: 'Availability', width: "auto", rendered: tooltiprenderer, hidden: true, filterable: false},
//            { text: 'Downtime', datafield: 'Downtime', columntype: 'number', width: "auto", rendered: tooltiprenderer, hidden: true, filterable: false},

            // "Operations"
            { text: 'Functions', datafield: 'Functions', width: "auto", rendered: tooltiprenderer, hidden: false},
//            { text: 'Function Notes', datafield: 'FunctionDescription', width: "auto", rendered: tooltiprenderer, hidden: true},
//            { text: 'Function Handle', datafield: 'FunctionHandle', width: "auto", rendered: tooltiprenderer, hidden: true, filterable: false},
            { text: 'Input types', datafield: 'InputTypes', width: "auto", rendered: tooltiprenderer, hidden: false},
            { text: 'Input formats', datafield: 'InputFormats', width: "auto", rendered: tooltiprenderer, hidden: true},
//            { text: 'Input Handle', datafield: 'InputHandle', width: "auto", rendered: tooltiprenderer, hidden: true, filterable: false},
            { text: 'Output types', datafield: 'OutputTypes', width: "auto", rendered: tooltiprenderer, hidden: false},
            { text: 'Output formats', datafield: 'OutputFormats', width: "auto", rendered: tooltiprenderer, hidden: true},
//            { text: 'Output Handle', datafield: 'OutputHandle', width: "auto", rendered: tooltiprenderer, hidden: true, filterable: false},

            // "Interfaces"
            { text: 'CLI', columntype: 'checkbox', datafield: 'InterfacesCommandLine', filtertype: 'bool', width: 75, rendered: tooltiprenderer, hidden: true},
            { text: 'Web UI', columntype: 'checkbox', datafield: 'InterfacesWebUI', filtertype: 'bool', width: 75, rendered: tooltiprenderer, hidden: false},
            { text: 'GUI', columntype: 'checkbox', datafield: 'InterfacesDesktopGUI', filtertype: 'bool', width: 75, rendered: tooltiprenderer, hidden: true},
            { text: 'REST', columntype: 'checkbox', datafield: 'InterfacesRESTAPI', filtertype: 'bool', width: 75, rendered: tooltiprenderer, hidden: false},
            { text: 'SOAP', columntype: 'checkbox', datafield: 'InterfacesSOAPAPI', filtertype: 'bool', width: 75, rendered: tooltiprenderer, hidden: true},
            { text: 'VMI', columntype: 'checkbox', datafield: 'InterfacesVMI', filtertype: 'bool', width: 75, rendered: tooltiprenderer, hidden: true},

            // "Platforms"
            { text: 'Windows', columntype: 'checkbox', datafield: 'PlatformsWindows', filtertype: 'bool', width: 100, rendered: tooltiprenderer, hidden: true},
            { text: 'Linux', columntype: 'checkbox', datafield: 'PlatformsLinux', filtertype: 'bool', width: 100, rendered: tooltiprenderer, hidden: true},
            { text: 'OS X', columntype: 'checkbox', datafield: 'PlatformsOSX', filtertype: 'bool', width: 100, rendered: tooltiprenderer, hidden: true},

            // "Usage"
            { text: 'Languages', datafield: 'Languages', filtertype: 'checkedlist', width: "auto", rendered: tooltiprenderer, hidden: true},

            // "Docs"
            { text: 'Docs', datafield: 'DocsHome', width: 75, rendered: tooltiprenderer, cellsrenderer: iconlinkrenderer, filterable: false },
            { text: 'Docs (cmd line)', datafield: 'DocsCommandLine', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconlinkrenderer, filterable: false },
            { text: 'Docs (REST)', datafield: 'DocsREST', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconlinkrenderer, filterable: false },
            { text: 'Docs (SOAP)', datafield: 'DocsSOAP', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconlinkrenderer, filterable: false },
            { text: 'Docs (SPARQL)', datafield: 'DocsSPARQL', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconlinkrenderer, filterable: false },
            { text: 'WSDL', datafield: 'WSDL', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconlinkrenderer, filterable: false },

            // "Contacts"
            { text: 'Helpdesk', datafield: 'Helpdesk', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconlinkrenderer, filterable: false},
//            { text: 'Contact Page', datafield: 'ContactPage', width: "auto", rendered: tooltiprenderer, hidden: true, cellsrenderer: simplelinkrenderer, filterable: false},
            { text: 'Contact person', datafield: 'ContactName', width: "auto", rendered: tooltiprenderer, cellsrenderer: linkrenderer, hidden: true, filterable: false},
            { text: 'Contact person (tel.)', datafield: 'ContactTel', width: "auto", rendered: tooltiprenderer, hidden: true, filterable: false},

            // "Credits"
            { text: 'Developer', datafield: 'Developer', width: "auto", rendered: tooltiprenderer, hidden: true },
            { text: 'Developer (interface)', datafield: 'DeveloperInterface', width: "auto", rendered: tooltiprenderer, hidden: true },
            { text: 'Contributors', datafield: 'Contributors', width: "auto", rendered: tooltiprenderer, hidden: true },
            { text: 'Institutions', datafield: 'Institutions', filtertype: 'checkedlist', width: "auto", rendered: tooltiprenderer, hidden: true },
            { text: 'Infrastructures', datafield: 'Infrastructures', filtertype: 'checkedlist', width: "auto", rendered: tooltiprenderer, hidden: true },
            { text: 'Funding', datafield: 'Funding', width: "auto", rendered: tooltiprenderer, hidden: true },
            { text: 'Work Packages', datafield: 'WorkPackages', filtertype: 'checkedlist', width: "auto", rendered: tooltiprenderer, hidden: true },


            // "Literature"
            { text: 'Publications (primary)', datafield: 'PublicationsPrimary', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconlinkrenderer, filterable: false},
            { text: 'Publications (other)', datafield: 'PublicationsOther', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconlinkrenderer, filterable: false},
            { text: 'Citations', datafield: 'Citations', width: "auto", rendered: tooltiprenderer, hidden: true, filterable: false},

            // "Restrictions"
            { text: 'Cost', datafield: 'Cost', filtertype: 'checkedlist', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconrenderer},
            { text: 'License', datafield: 'License', filtertype: 'checkedlist', width: "auto", rendered: tooltiprenderer, hidden: true},
            { text: 'Terms Of Use', datafield: 'TermsOfUse', width: 75, rendered: tooltiprenderer, hidden: true, cellsrenderer: iconlinkrenderer, filterable: false },

            // "Registration"
//            { text: 'Registrant Name', datafield: 'RegistrantName', width: "auto", rendered: tooltiprenderer, hidden: true},
//            { text: 'Registrant Email', datafield: 'RegistrantEmail', width: "auto", rendered: tooltiprenderer, hidden: true, cellsrenderer: simplelinkrenderer, filterable: false },
//            { text: 'Addition Date', datafield: 'AdditionDate', width: "auto", rendered: tooltiprenderer, hidden: true},
//            { text: 'Last Update', datafield: 'LastUpdate', width: "auto", rendered: tooltiprenderer, hidden: true},

        // "Search terms"
        { text: 'Search terms', datafield: 'SearchTerms', hidden: true}];



    // initialize jqxGrid
    $("#jqxgrid").jqxGrid
        ({
            // Filter
            width: "100%",
            height: "100%",
            filterable: true,
            showfiltermenuitems: true,
            showfilterrow: true,
            autoshowfiltericon: true,
            source: dataAdapter,
            theme: theme,
            localization: customiseGrid(),
            pageable: true,
            autoheight: false,
            sortable: true,
            altrows: true,
            enabletooltips: true,
            editable: false,
            selectionmode: 'none',
            enablebrowserselection: 'true',
            columnsresize: true,
            columnsreorder: true,
            columnsmenu: false,
            autoshowcolumnsmenubutton: false,
            showfiltercolumnbackground: true,
//                autorowheight: true,
            pagesize: 25,
            pagesizeoptions: ['10', '25', '50', '100', '250', '500', '1000'],

//            rowdetails: true,
            rowdetailstemplate:
            {
//                rowdetails: "<div style='margin: 10px;'><ul style='margin-left: 30px;'><li class='title'></li><li>Description</li></ul><div class='information'></div><div class='description'></div></div>",
                rowdetails: "<div style='margin: 10px;'><ul style='margin-left: 30px;'><li>Summary</li><li>Description</li><li>Credits</li></ul><div class='information'></div><div class='description'></div><div class='credits'></div></div>",
                rowdetailsheight: 200
            },
            initrowdetails: initrowdetails,


            columns: columns


            /* jqwidget "column group" functions is still unreliable
             To try again in future (on new jqwidget release):
             1) Uncomment this block
             2) Add e.g. "columngroup: 'Registration'" to column definitions above]
             */
//                ,columngroups:
//                        [
//                            { text: 'Summary', align: 'center', name: 'Summary' },
//                            { text: 'Status', align: 'center', name: 'Status' },
//                            { text: 'Operations', align: 'center', name: 'Operations' },
//                            { text: 'Usage', align: 'center', name: 'Usage' },
//                            { text: 'Docs', align: 'center', name: 'Docs' },
//                            { text: 'Contacts', align: 'center', name: 'Contacts' },
//                            { text: 'Credits', align: 'center', name: 'Credits' },
//                            { text: 'Restrictions', align: 'center', name: 'Restrictions' },
//                            { text: 'Registration', align: 'center', name: 'Registration' }]
        });



    /* Set alignment of column headers */
    var setAlignment = function (align)
    {
        $("#jqxgrid").jqxGrid('beginupdate');
        for (var index = 0; index < columns.length; index++)
        {
            $("#jqxgrid").jqxGrid('setcolumnproperty', columns[index].datafield, 'align', align);
//                    $("#jqxgrid").jqxGrid('setcolumnproperty', columns[index].datafield, 'cellsalign', align);
        }
        $("#jqxgrid").jqxGrid('endupdate');
    }



    $("#jqxgrid").on("bindingcomplete", function (event)
    {
        setAlignment('center');
        $('#jqxgrid').jqxGrid('sortby', 'InterfacesRESTAPI', 'desc');
//        $('#jqxgrid').jqxGrid('sortby', 'SearchTerms', 'desc');
    });


    $("#jqxgrid").on("filter", function (event)
    {


        var filterinfo = $("#jqxgrid").jqxGrid('getfilterinformation');
        if(filterinfo.length == 0)
        {
            $('#clearfilter').val("(filters off)");
            $('#clearfilter').css('color', "#f8fcfe");
            $('#clearfilter').css('backgroundColor', "#e8e8e8");
        }
else
        {
            $('#clearfilter').val("Clear filters");
            $('#clearfilter').css('color', "black");
        $('#clearfilter').css('backgroundColor', "#33B7DE");  /* was red */

            }

    });


    // Filter

//    // This block includes the uncommented code only
//    $("#jqxgrid").bind("filter", function (event)
//    {
//        var filterinfo = $("#jqxgrid").jqxGrid('getfilterinformation');
//        for (i = 0; i < filterinfo.length; i++)
//        {
//            var eventData = "Filter Column: " + filterinfo[i].filtercolumntext;
//        }
//    });
//
//
//    // Original block including comments
////    $('#events').jqxPanel({ width: 300, height: 80, theme: theme });
//    $("#jqxgrid").bind("filter", function (event)
//    {
////        $("#events").jqxPanel('clearcontent');
////        var eventData = "Triggered 'filter' event";
//
//        var filterinfo = $("#jqxgrid").jqxGrid('getfilterinformation');
//        for (i = 0; i < filterinfo.length; i++)
//        {
//            var eventData = "Filter Column: " + filterinfo[i].filtercolumntext;
////            $('#events').jqxPanel('prepend', '<div style="margin-top: 5px;">' + eventData + '</div>');
//        }
//    });

    $('#filterbackground').jqxCheckBox({ checked: true, height: 25, theme: theme });
    $('#filtericons').jqxCheckBox({ checked: false, height: 25, theme: theme });

    // show/hide filter background
    $('#filterbackground').bind('change', function (event)
    {
        $("#jqxgrid").jqxGrid({ showfiltercolumnbackground: event.args.checked });
    });

    // show/hide filter icons
    $('#filtericons').bind('change', function (event)
    {
        $("#jqxgrid").jqxGrid({ autoshowfiltericon: !event.args.checked });
    });

//    $('#clearfilter').jqxButton({ height: 25, theme: theme });

    // clear the filtering.
    $('#clearfilter').click(function ()
    {
        $("#jqxgrid").jqxGrid('clearfilters');
        $('#SearchBox').val('');
    });




    // create buttons, listbox and the columns chooser dropdownlist
//    $("#applyfilter").jqxButton({ theme: theme });
    $("#clearfilter").jqxButton({ theme: theme, width: 125});

    // Jon HERE-1

    $("#columnchooser").jqxDropDownList({ autoDropDownHeight: true, selectedIndex: 0, theme: theme, width: 140, height: 25,
        source: [{ label: 'Type', value: 'Type' },
            { label: 'Collection', value: 'Collection' },
//            { label: 'Uses', value: 'Uses' },
            { label: 'Topics', value: 'Topics' },
            { label: 'Tags', value: 'Tags' },
            { label: 'Maturity', value: 'Maturity' },
            { label: 'Functions', value: 'Functions' },
            { label: 'Input types', value: 'InputTypes' },
            { label: 'Input formats', value: 'InputFormats' },
            { label: 'Input handle', value: 'InputHandle' },
            { label: 'Output types', value: 'OutputTypes' },
            { label: 'Output formats', value: 'OutputFormats' },
            { label: 'Output handle', value: 'OutputHandle' },
            { label: 'Command line', value: 'InterfacesCommandLine' },
            { label: 'Web UI', value: 'InterfacesWebUI' },
            { label: 'Desktop GUI', value: 'InterfacesDesktopGUI' },
            { label: 'REST API', value: 'InterfacesRESTAPI' },
            { label: 'SOAP API', value: 'InterfacesSOAPAPI' },
            { label: 'VMI', value: 'InterfacesVMI' },
            { label: 'Windows', value: 'PlatformsWindows' },
            { label: 'Linux', value: 'PlatformsLinux' },
            { label: 'OS X', value: 'PlatformsOSX' },
            { label: 'Languages', value: 'Languages' },
            { label: 'Cost', value: 'Cost' },
            { label: 'License', value: 'License' }
        ]
    });


         // Jon HERE-2
}


/*
    Customise grid using localizations
 */
function customiseGrid()
    {
    var localizationobj = {};
    localizationobj.filterselectstring = "Filters";
    return localizationobj;
}



// Define tooltips
var tips=
{
    // Menus
    "MenuAbout":"Read about the tools registry",
    "MenuExport":"Export data in a variety of formats (2MB limit!)",
    "MenuPartners":"Links to our partners",
    "MenuShow":"Control the information (columns) you see in the grid",
    "ClearFilters":"Clear all the filters (to see all content in the grid)",
    "SearchBox":"Control the software (rows) you see in the grid.  Enter filter terms here.",
    "Name":"Name of the software",
//    "EntryID":"Stable unique accession (URI) identifying a registry entry",
    "Type": "Basic type of software entry (tool, database, package etc.)",
    "Collection":"Name of the bundle, package, suite etc. that the software belongs to",
//    "Uses":"Name of another registry entry that this software uses, e.g. provides an interface to",
    "Description":"Short description of the software",
    "Topics":"General domain(s) the software serves",
    "Tags":"Miscellaneous keywords describing the software",
    "Version":"Version of the software, e.g. tool version number",
    "Download":"Tool or data download instructions (URL)",

    "Maturity":"Software maturity: one of 'Alpha', 'Beta' or 'Production'",
//    "Availability":"Whether a Web service is available for use",
//    "Downtime":"The percentage of time a Web service has been unavailable",
    "Functions":"Software function(s): terms describing what it does",
//    "FunctionDescription":"Free-text description of the function",
//    "FunctionHandle":"One of WSDL operation name (SOAP services), URL scheme (REST services) or option/flag (command-line)",
    "Inputtypes":"Type(s) of primary input data of the software",
    "Inputformats":"Allowed format(s) of the primary inputs",
//    "InputHandle":"Input identifier, e.g. command-line flag, parameter name etc.",
    "Outputtypes":"Type(s) of primary output data of the software",
    "Outputformats":"Allowed format(s) of the primary outputs",
//    "OutputHandle":"Output identifier, e.g. command-line flag, parameter name etc.",

//    "Interfaces":"Tool interfaces: REST service, SOAP service, Web application, Command-line tool or Desktop GUI",
    "CLI":"Command-line interface",
    "WebUI":"Web user interface",
    "GUI":"Desktop graphical user interface",
    "REST":"REST programmatic interface",
    "SOAP":"SOAP programmatic interface",
    "VMI":"Virtual machine interface",

    "Windows":"Support for MS Windows platform (downloadable packages only)",
    "Linux":"Support for Linux platform (downloadable packages only)",
    "OSX":"Support for OS X platform (downloadable packages only)",

    "Languages":"Languages (for APIs etc.) or technologies (for Web applications, applets etc.)",
    "Docs":"Tool documentation main page (URL)",
    "Docs(cmdline)":"Command-line documentation (URL)",
    "Docs(REST)":"REST service documentation (URL)",
    "Docs(SOAP)":"SOAP service documentation (URL)",
    "Docs(SPARQL)":"SPARQL service documentation (URL)",
    "TermsOfUse":"Link to terms of use (URL)",
    "WSDL":"Location of WSDL (URL)",
    "Helpdesk":"Email/URL of helpdesk",
//    "ContactPage":"URL of page with general contact details",
    "Contactperson":"Name/email of primary contact person",
//    "Contact":"Email of contact person",
//    "ContactID":"ID of contact person",
    "Contactperson(tel.)":"Telephone no. of the primary contact person",
    "Developer":"Name(s) of the people that developed the software",
    "Developer(interface)":"Name(s) of the people that developed the software interface",
    "Contributors":"Name(s) of other people that contributed to the software development",
    "Institutions":"Institution(s) that developed or provide the tool",
    "Infrastructures":"Research infrastructure(s) in which the tool was developed or provided",
    "Funding":"Details of grant funding",
    "WorkPackages":"Work packages in which the tool was developed",
    "Publications(primary)":"The primary publication that should be cited",
    "Publications(other)":"Other publications that should be cited",
    "Citations":"Number of citations of the primary publication",
    "Cost":"Cost of purchase: one of 'Free' (possibly with clauses) or 'Not free'",
    "License":"Tool license"
//    "RegistrantName":"Name of person who registered the software",
//    "RegistrantEmail":"Email address of person who registered the software",
//    "AdditionDate":"Date the software was registered",
//    "LastUpdate":"Date the metadata was last updated"
};


/*
    Creates tool tips for the grid menu
 */

function create_tooltips()
{
    // Create jqxTooltip
//    $("#MenuMyTools").jqxTooltip({ content: 'Manage your own tools', position: 'mouse', theme: theme });
//    $("#MenuMyToolsLogin").jqxTooltip({ content: 'You will need to login to use items in this menu', position: 'mouse', theme: theme });
//    $("#MenuMyToolsShow").jqxTooltip({ content: 'This will show in the grid below only tools you have registered', position: 'mouse', theme: theme });
//    $("#MenuMyToolsRegister").jqxTooltip({ content: 'Register new tools, update tool descriptions or delete old entries', position: "right', theme: theme });
//    $("#MenuExport").jqxTooltip({ content: 'Export tool information in a variety of formats', position: "right', theme: theme });
//    $("#tipMenuUseCases").jqxTooltip({ content: getTip("MenuUseCases"), position: 'bottom', theme: theme});

    var theme = getDemoTheme();

    // Menus
    $("#tipMenuAbout").jqxTooltip({ content: getTip("MenuAbout"), position: 'bottom', theme: theme });
    $("#tipMenuExport").jqxTooltip({ content: getTip("MenuExport"), position: 'bottom', theme: theme });
    $("#tipMenuPartners").jqxTooltip({ content: getTip("MenuPartners"), position: 'bottom', theme: theme });
    $("#tipMenuFilters").jqxTooltip({ content: getTip("MenuFilters"), position: 'bottom', theme: theme });
    $("#tipClearFilters").jqxTooltip({ content: getTip("ClearFilters"), position: 'top', top: '-18px', left: '45px', theme: theme });
    $("#tipMenuShow").jqxTooltip({ content: getTip("MenuShow"), position: 'right', left: '18px', theme: theme });
    $("#tipSearchBox").jqxTooltip({ content: getTip("SearchBox"), position: 'left', left: '12px', top: '20px',  theme: theme });

    // Tool attributes
    makeTip("tipName", "Name");
//    makeTip("tipEntryID","EntryID");
    makeTip("tipType", "Type");
    makeTip("tipCollection","Collection");
//    makeTip("tipUses","Uses");
    makeTip("tipDescription","Description");
    makeTip("tipTopics","Topics");
    makeTip("tipTags","Tags");
    makeTip("tipVersion","Version");
    makeTip("tipDownload","Download");

    makeTip("tipMaturity","Maturity");
//    makeTip("tipAvailability","Availability");
//    makeTip("tipDowntime","Downtime");
    makeTip("tipFunctions","Functions");
//    makeTip("tipFunctionDescription","FunctionDescription");
//    makeTip("tipFunctionHandle","FunctionHandle");
    makeTip("tipInputTypes","Inputtypes");
    makeTip("tipInputFormats","Inputformats");
//    makeTip("tipInputHandle","InputHandle");
    makeTip("tipOutputTypes","Outputtypes");
    makeTip("tipOutputFormats","Outputformats");
//    makeTip("tipOutputHandle","OutputHandle");

//    makeTip("tipInterfaces","Interfaces");
    makeTip("tipInterfacesCommandLine","CLI");
    makeTip("tipInterfacesWebUI","WebUI");
    makeTip("tipInterfacesDesktopGUI","GUI");
    makeTip("tipInterfacesRESTAPI","REST");
    makeTip("tipInterfacesSOAPAPI","SOAP");
    makeTip("tipInterfacesVMI","VMI");
    makeTip("tipPlatformsWindows","Windows");
    makeTip("tipPlatformsLinux","Linux");
    makeTip("tipPlatformsOSX","OSX");
    makeTip("tipLanguages","Languages");
    makeTip("tipDocsHome","Docs");
    makeTip("tipDocsCommandLine","Docs(cmdLine)");
    makeTip("tipDocsREST","Docs(REST)");
    makeTip("tipDocsSOAP","Docs(SOAP)");
    makeTip("tipDocsSPARQL","Docs(SPARQL)");
    makeTip("tipWSDL","WSDL");
    makeTip("tipHelpdesk","Helpdesk");
//    makeTip("tipContactPage","ContactPage");
    makeTip("tipContactName","Contactperson");
//    makeTip("tipContact","Contact");
//    makeTip("tipContactID","ContactID");
    makeTip("tipContactTel","Contactperson(tel.)");
    makeTip("tipDeveloper","Developer");
    makeTip("tipDeveloperInterface","Developer(interface)");
    makeTip("tipContributors","Contributors");
    makeTip("tipInstitutions","Institutions");
    makeTip("tipInfrastructures","Infrastructures");
    makeTip("tipFunding","Funding");
    makeTip("tipWorkPackages","WorkPackages");
    makeTip("tipPublicationsPrimary","Publications(primary)");
    makeTip("tipPublicationsOther","Publications(other)");
    makeTip("tipCitations","Citations");
    makeTip("tipCost","Cost");
    makeTip("tipLicense","License");
    makeTip("tipTermsOfUse","TermsOfUse");
//    makeTip("tipRegistrantName","RegistrantName");
//    makeTip("tipRegistrantEmail","RegistrantEmail");
//    makeTip("tipAdditionDate","AdditionDate");
//    makeTip("tipLastUpdate","LastUpdate");
}


function makeTip(id, key)
{
    var theme = getDemoTheme();

    var obj = document.getElementById(id);
    $(obj).jqxTooltip({ content: getTip(key), position: 'right', left: "120px", top:"13px", theme: theme });
}


function getTip(key)
{
    return tips[key];
}


// Tooltip renderer on column headers
function tooltiprenderer (element)
{
    var tip=$(element).text().replace(/ /g, '');
    $(element).jqxTooltip({position: 'mouse', content: getTip(tip)});
//    $(element).jqxTooltip({position: 'mouse', content: getTip($(element).text()) });

//    $(element).jqxTooltip({position: 'mouse', content: someContent });

//$(element).jqxTooltip({position: 'mouse', content: $(element).text() });
}





//<script type="text/javascript">
//    <!-- Tooltip renderer on column headers -->
//var tooltiprenderer = function (element)
//{
//
//    $(element).jqxTooltip({position: 'mouse', content: getTip($(element).text()) });
//
//
////    $(element).jqxTooltip({position: 'mouse', content: someContent });
//
////$(element).jqxTooltip({position: 'mouse', content: $(element).text() });
//}
//</script>
//
