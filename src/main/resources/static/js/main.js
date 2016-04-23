var username;


$(document).ready(function() {
    $('#particles').particleground({
        dotColor: '#F0F4FF',
        lineColor: '#F0F4FF' /*f5f7ff*/
    });
    
    vex.dialog.buttons.YES.text = "let's go!"
    vex.dialog.prompt({
        message: 'What twitter handle do you wanna search?',
        placeholder: 'username',
        callback: function(value) {
            username = value;
            if (value !== false) {  // if valid username was entered
                $("#usernameInput").val(value);
            }
        }
    });
});

/*------------------ TWEET COMPOSITION ------------------*/
changeURL = function(e) {
    var tweetContent = document.getElementById("tweetInput").value;
    var tcURI = encodeURI(tweetContent);
    var tweetURL = "https://twitter.com/intent/tweet?text=" + tcURI; 
    e.href = tweetURL;
};


/*------------------ GRAPH TOGGLE ------------------*/
var statsOut = false;

$("#topsugslist li, #topsugsslide").click(function() { 
    $("#trendgraphtitle").html($(this).text() + " Trend Graph:");
    
    if (!statsOut) {
        $("#trendsgraph").slideToggle(400);
        $("#suggestions").attr("class", "col col-sm-7");
        $("#trendsgraph").css("display", "block");
        statsOut = true;
    }
});

$("#closeTrendGraph").click(function() { 
    $("#trendsgraph").toggle();
    $("#suggestions").attr("class", "col col-sm-12");
    $("#trendsgraph").css("display", "none");
    statsOut = false;
});


/*------------------ SUGGESTIONS LIST SLIDE ------------------*/
$(function () {
    var messages = ["puppies", "#springWeekend", "CS32", "essays", "#bostonMarathon"],
    index = 0;
    function cycle() {
        $("#topsugsslide").html(messages[index]);
        index++;
        if (index === messages.length) {
            index = 0;
        }
        setTimeout(cycle, 3000);
    }
    cycle();
});


/*------------------ USERNAME INPUT ------------------*/
var editingUsername = false;

$(".fa").click(function() {
    if (editingUsername) {  // done editing username
        editingUsername = false;
        $("#usernameEdit").attr("class", "fa fa-pencil");
        $("#usernameInput").prop("readonly", true);
        $("#usernameInput").css("border-bottom","none");
        username = $("#usernameInput").val();

        // sending the username to the backend
        var postParameters = {'user': username};
        $.get("/userTweets", postParameters, function(responseJSON) {
            console.log(responseJSON);
            var parsedResponse = JSON.parse(responseJSON);
            console.log(parsedResponse.words);

        })
    } else {
         editingUsername = true;
        $("#usernameEdit").attr("class", "fa fa-check");
        $("#usernameInput").prop("readonly", false);
        $("#usernameInput").css("border-bottom","1px solid #162252");
    }
});


/*------------------ SLIDE OR LIST ------------------*/
$("[name='my-checkbox']").bootstrapSwitch();
$(".bootstrap-switch-label").html("<div></div>");
$(".bootstrap-switch").css("background","#162252");

$("input[name='my-checkbox']").on("switchChange.bootstrapSwitch", function(event, state) {
    if (state) {    // if switched to slides
        $("#suggestions").css("transform","translateY(50%)");
    } else {        // if switched to lists
        $("#suggestions").css("transform","translateY(15%)");
    }
    $("#topsugslist").slideToggle(500);
    $("#topsugsslide").slideToggle(500);
});


/*------------------ SUGGESTION TYPE ------------------*/
$("input[type='radio']").click(function(){
    if ($(this).is(":checked")) {
        //alert($(this).val());
    }
});