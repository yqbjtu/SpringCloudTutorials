$(document).ready(function() {
    $.ajax({
        url: "http://localhost:6608/user/users/2"
    }).then(function(data, status, jqxhr) {
       console.log(data);
       $('.user-id').append(data.id);
       $('.user-name').append(data.name);
       console.log(jqxhr);
    });
});