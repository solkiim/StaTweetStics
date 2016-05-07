var username;
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
});

function dialogBoxes() {
    // username dialog prompt
    vex.dialog.buttons.YES.text = "let's go!"
    
    vex.dialog.confirm({
        message: 'Click on the suggestions to see trend graphs!'
    });
    
    vex.dialog.buttons.YES.text = "i'm ready!"
    
    getTopTrending();
    
    vex.dialog.prompt({
        message: 'What twitter handle do you wanna search?',
        placeholder: 'username',
        callback: function(value) {
            if (value !== false) {  // if valid username was entered
                username = value;
                $("#usernameInput").val(value); // set username
                getYourTrending();
            } else {
                $("#yourtrending").prop("checked", false);
                $("#twittertrending").prop("checked", true);
                //getTopTrending();
            }
        }
    });
}

/*------------------ GETTING TRENDING LISTS ------------------*/
function getTopTrending() {
    $.get("/topTweets", {}, function(responseJSON) {
        var parsedResponse = JSON.parse(responseJSON);
        
        yourtrending = {"enter":[], "a twitter handle":[], "to see":[], "cool stats!":[]};
        twittertrending = {};
        
        var parsedTTrending = parsedResponse.twitterTrending;
        
        for (var i = 0; i < 5; i++) {
            twittertrending[parsedTTrending[i]] = parsedTTrending[i];
        }

        displayedSugs = twittertrending;
        topSugList();
        topSugSlide();
    })
}

// updating username in back end and getting new data
function getYourTrending() {    
    // sending the username to the backend
    var postParameters = {'user': username};
    $.get("/userTweets", postParameters, function(responseJSON) {
        var parsedResponse = JSON.parse(responseJSON);
        console.log(parsedResponse);
        
        // clear data for previous username
        yourtrending = {};
        
        // switch the suggestion type to your trending
        $("#yourtrending").prop("checked", true);
        $("#twittertrending").prop("checked", false);
        
        // populating your trending list
        var parsedYourTrending = parsedResponse.yourTrending;
        for (var i = 0; i < parsedYourTrending.length; i++) {
            yourtrending[parsedYourTrending[i].text] = parsedYourTrending[i].data;
        }
        
        displayedSugs = yourtrending;
        topSugList();
        topSugSlide();
    })
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

$(body).on("click", "#topsugslist li, #topsugsslide"), function() { 
    alert("CLICKED");
    $("#statsTitle").html($(this).text() + " Trend Graph:");
    
    if (!statsOut) {
        $("#tweetStats").slideToggle(400);
        $("#suggestions").attr("class", "col col-sm-7");
        $("#tweetStats").css("display", "block");
        statsOut = true;
    }
});

$("#closeStats").click(function() { 
    $("#tweetStats").toggle();
    $("#suggestions").attr("class", "col col-sm-12");
    $("#tweetStats").css("display", "none");
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
    return setInterval(run, 2500);
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
        getYourTrending();
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
        topSugSlide();
    } else {        // if switched to lists
        topSugList();
    }
    $("#topsugslist").slideToggle(500);
    $("#topsugsslide").slideToggle(500);
});


/*------------------ SUGGESTION TYPE ------------------*/
$("input[name='trendtype']").click(function(){
    if ($(this).is(":checked")) {
        console.log($(this).val());
        if ($(this).val() === "yourtrending") {
            displayedSugs = yourtrending;
        } else if ($(this).val() === "twittertrending") {
            displayedSugs = twittertrending;
        }
        topSugList();
        topSugSlide();
    }
});