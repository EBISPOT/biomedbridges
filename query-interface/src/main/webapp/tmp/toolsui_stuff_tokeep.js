
/**
 * Created by IntelliJ IDEA.
 * User: jison
 * Date: 3/6/14
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */

  // Jon HERE-1
    $("#filterbox").jqxListBox({ checkboxes: true, theme: theme, width: 140, height: 350 });


// Jon HERE-2
    // updates the listbox with unique records depending on the selected column.
var updateFilterBox = function (datafield)
{
    var filterBoxAdapter = new $.jqx.dataAdapter(source,
        {
            uniqueDataFields: [datafield],
            autoBind: true
        });
    var uniqueRecords = filterBoxAdapter.records;

    uniqueRecords.splice(0, 0, '(Select All)');
    $("#filterbox").jqxListBox({ source: uniqueRecords, displayMember: datafield });
    $("#filterbox").jqxListBox('checkAll');
}
updateFilterBox('Type');

// handle select all item.
var handleCheckChange = true;
$("#filterbox").on('checkChange', function (event)
{
    if (!handleCheckChange)
        return;
    if (event.args.label != '(Select All)')
    {
        handleCheckChange = false;
        $("#filterbox").jqxListBox('checkIndex', 0);
        var checkedItems = $("#filterbox").jqxListBox('getCheckedItems');
        var items = $("#filterbox").jqxListBox('getItems');
        if (checkedItems.length == 1)
        {
            $("#filterbox").jqxListBox('uncheckIndex', 0);
        }
        else if (items.length != checkedItems.length)
        {
            $("#filterbox").jqxListBox('indeterminateIndex', 0);
        }
        handleCheckChange = true;
    }
    else
    {
        handleCheckChange = false;
        if (event.args.checked)
        {
            $("#filterbox").jqxListBox('checkAll');
        }
        else
        {
            $("#filterbox").jqxListBox('uncheckAll');
        }
        handleCheckChange = true;
    }
});
// handle columns selection.
$("#columnchooser").on('select', function (event)
{
    updateFilterBox(event.args.item.value);
});
// builds and applies the filter.

var applyFilter = function (datafield)
{
    $("#jqxgrid").jqxGrid('clearfilters');
    var filtertype = 'stringfilter';
    if (datafield == 'date') filtertype = 'datefilter';
    if (datafield == 'price' || datafield == 'quantity') filtertype = 'numericfilter';
    var filtergroup = new $.jqx.filter();
    var checkedItems = $("#filterbox").jqxListBox('getCheckedItems');
    if (checkedItems.length == 0)
    {
        var filter_or_operator = 1;
        var filtervalue = "Empty";
        var filtercondition = 'equal';
        var filter = filtergroup.createfilter(filtertype, filtervalue, filtercondition);
        filtergroup.addfilter(filter_or_operator, filter);
    }
    else
    {
        for (var i = 0; i < checkedItems.length; i++)
        {
            var filter_or_operator = 1;
            var filtervalue = checkedItems[i].label;
            var filtercondition = 'equal';
            var filter = filtergroup.createfilter(filtertype, filtervalue, filtercondition);
            filtergroup.addfilter(filter_or_operator, filter);
        }
    }
    $("#jqxgrid").jqxGrid('addfilter', datafield, filtergroup);
    $("#jqxgrid").jqxGrid('applyfilters');
}
// clears the filter.
$("#clearfilter").click(function ()
{
    $("#jqxgrid").jqxGrid('clearfilters');
});

// applies the filter.
//    $("#applyfilter").click(function ()
//    {
//        var dataField = $("#columnchooser").jqxDropDownList('getSelectedItem').value;
//        applyFilter(dataField);
//    });
