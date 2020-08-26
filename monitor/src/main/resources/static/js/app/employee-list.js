$('#logout-action').click(function () {
    $('#logout-form').submit();
});

$(document).on("click", "#delete-item-btn", function () {
    const sn = $(this).data('id');
    $('#employeeId').val(sn);
});

$(document).ready(function()
{
    $('#dt-basic-example').dataTable(
        {
            responsive: true,
            searching: true,
            ordering: true
        });
});
