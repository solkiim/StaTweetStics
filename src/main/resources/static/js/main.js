var username;
var topsugs = {};
var yourtrending = {};
var twittertrending = {};
var displayedSugs;    // sugs displayed currently

$(document).ready(function() {
    // particle background setup
    $('#particles').particleground({
        dotColor: '#F0F4FF',
        lineColor: '#F0F4FF' /*f5f7ff*/
    });
    
    dialogBoxes();
    
    // set up defaults if no username input
    topsugs = {"puppies":[0], "#springWeekend":[0], "CS32":[0], "finals":[0], "#bostonMarathon":[0]}
    yourtrending = topsugs;
    twittertrending = topsugs;
    displayedSugs = topsugs;
    
    topSugSlide();  // start top sugs slide
});

function dialogBoxes() {
    // username dialog prompt
    vex.dialog.buttons.YES.text = "let's go!"
    
    vex.dialog.confirm({
        message: 'Click on the suggestions to see trend graphs!'
    });
    
    vex.dialog.buttons.YES.text = "i'm ready!"
    
    vex.dialog.prompt({
        message: 'What twitter handle do you wanna search?',
        placeholder: 'username',
        callback: function(value) {
            if (value !== false) {  // if valid username was entered
                username = value;
                $("#usernameInput").val(value); // set username
                updateUsername();
            }
        }
    });
}

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
//    alert("CLICKED");
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
// populates the top sug list
function topSugList() {
    var listhtml = "";
    var wordlist = Object.keys(displayedSugs);
    for (var i = 0; i < wordlist.length; i++) {
        listhtml += ("<li>" + wordlist[i] + "</li>");
    }
    $("#topsugslist").html(listhtml);
}

var timer;  // keeps track of current cycle's interval

// generates and starts the top sug slide
function topSugSlide() {
    if (timer !== undefined) {
        clearInterval(timer);   // stop previous cycle
    }
    timer = cycle();    // set new cycle
}

function cycle() {
    var i = 0;
    var wordlist = Object.keys(displayedSugs);
    function run() {
        $("#topsugsslide").html(wordlist[i]);
        i++;
        if (i === wordlist.length) {
            i = 0;
        }
    }
    return setInterval(run, 3000);
}


/*------------------ USERNAME INPUT ------------------*/
var editingUsername = false;

// editing username via page form
$(".fa").click(function() {
    if (editingUsername) {  // done editing username
        editingUsername = false;
        $("#usernameEdit").attr("class", "fa fa-pencil");
        $("#usernameInput").prop("readonly", true);
        $("#usernameInput").css("border-bottom","none");
        username = $("#usernameInput").val();
        updateUsername();
    } else {
         editingUsername = true;
        $("#usernameEdit").attr("class", "fa fa-check");
        $("#usernameInput").prop("readonly", false);
        $("#usernameInput").css("border-bottom","1px solid #162252");
    }
});

// updating username in back end and getting new data
function updateUsername() {
    // sending the username to the backend
    var postParameters = {'user': username};
    $.get("/userTweets", postParameters, function(responseJSON) {
        var parsedResponse = JSON.parse(responseJSON);
        
        // clear data for previous username
        topsugs = {};
        yourtrending = {};
        twittertrending = {};
        
        // populating top sugs list
        var parsedTopSugs = parsedResponse.topSuggs;
        for (var i = 0; i < parsedTopSugs.length; i++) {
            topsugs[parsedTopSugs[i].text] = parsedTopSugs[i].data;
        }
        
        // populating your trending list
        var parsedYourTrending = parsedResponse.yourTrending;
        for (var i = 0; i < parsedYourTrending.length; i++) {
            yourtrending[parsedYourTrending[i].text] = parsedYourTrending[i].data;
        }
        
        // populating twitter trending list
        var parsedTTrending = parsedResponse.twitterTrending;
        for (var i = 0; i < 5; i++) {
            twittertrending[parsedTTrending[i]] = parsedTTrending[i];
        }
        
        displayedSugs = topsugs;
        topSugList();
        topSugSlide();
    })
}


/*------------------ SLIDE OR LIST ------------------*/
$("[name='my-checkbox']").bootstrapSwitch();
$(".bootstrap-switch-label").html("<div></div>");
$(".bootstrap-switch").css("background","#162252");

$("input[name='my-checkbox']").on("switchChange.bootstrapSwitch", function(event, state) {
    if (state) {    // if switched to slides
        topSugSlide();
    } else {        // if switched to lists
        topSugList();
    }
    $("#topsugslist").slideToggle(500);
    $("#topsugsslide").slideToggle(500);
});


/*------------------ SUGGESTION TYPE ------------------*/
$("input[type='radio']").click(function(){
    if ($(this).is(":checked")) {
        if ($(this).val() === "topsugs") {
            displayedSugs = topsugs;
        } else if ($(this).val() === "yourtrending") {
            displayedSugs = yourtrending;
        } else if ($(this).val() === "twittertrending") {
            displayedSugs = twittertrending;
        }
        topSugList();
        topSugSlide();
    }
});