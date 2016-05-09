var username;

var indivuser;
var indivuserlikes = {};
var indivuserretweets = {};

var compareusers = {};
var usersToCompare = [];
var userCount = 1;

var indiv = true;
var RTnotLike = true;

var displayedSugs;    // sugs displayed currently

$(document).ready(function() {
    indiv = true;
    
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
        message: 'Click on the suggestions to see statistics!'
    });
    
    vex.dialog.buttons.YES.text = "i'm ready!"
        
    vex.dialog.prompt({
        message: 'What twitter handle do you wanna search?',
        placeholder: 'username',
        callback: function(value) {
            if (value !== false) {  // if valid username was entered
                username = value;
                $(".usernameInput").val(value); // set username
                getIndivUser();
            } else {
                indivuser = {"enter":[], "a twitter handle":[], "to see":[], "super cool":[], "stats!":[]};
                compareusers = indivuser;
                displayedSugs = indivuser;
                topSugList();
            }
        }
    });
}

/*------------------ GETTING TRENDING LISTS ------------------*/
function getCompareUsers() {
    $.get("/topTweets", {}, function(responseJSON) {
        var parsedResponse = JSON.parse(responseJSON);
        compareusers = {};
        var parsedTTrending = parsedResponse.twitterTrending;
        
        for (var i = 0; i < 5; i++) {
            compareusers[parsedTTrending[i]] = parsedTTrending[i];
        }

        displayedSugs = compareusers;
        topSugList();
        topSugSlide();
    })
}

// updating username in back end and getting new data
function getIndivUser() {    
    // sending the username to the backend
    var postParameters = {'user': username};
    $.get("/userTweets", postParameters, function(responseJSON) {
        var parsedResponse = JSON.parse(responseJSON);
        console.log(parsedResponse);
        
        // clear data for previous username
        indivuser = {};
        
        // switch the suggestion type to your trending
        $("#indivuser").prop("checked", true);
        $("#compareusers").prop("checked", false);
        
        // populating your trending list
        var parsedYourTrending = parsedResponse.yourTrending;
        
        console.log(parsedYourTrending);
        
        for (var i = 0; i < parsedYourTrending.length; i++) {
            indivuser[parsedYourTrending[i].text] = parsedYourTrending[i].data;
        }
        
        displayedSugs = indivuser;
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


/*------------------ STATS TOGGLE ------------------*/
var statsOut = false;

$(document).on("click", "#topsugslist li, #topsugsslide", function() { 
    $("#statsTitle").html($(this).text() + " Stats:");
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
$("#usernameEdit").click(function() {
    if (editingUsername) {  // done editing username
        editingUsername = false;
        $("#usernameEdit").attr("class", "fa fa-pencil");
        $(".usernameInput").prop("readonly", true);
        $(".usernameInput").css("border-bottom","none");
        $("#usernameAdd").css("display", "none");
        username = $(".usernameInput").val();
        
        if (indiv) {
            getIndivUser();
        } else {
            usersToCompare = [];
            $('#inputGroup > input').each(function () {
                if (this.value != "") {
                    usersToCompare.push(this.value);
                }  
            });
            //getCompareUsers();
        }
    } else {
        editingUsername = true;
        $("#usernameEdit").attr("class", "fa fa-check");
        $(".usernameInput").prop("readonly", false);
        $(".usernameInput").css("border-bottom","1px solid #162252");
        
        userCount = 1;
        if (!indiv) {
            $("#usernameAdd").css("display", "inline-block");
        }
    }
});


/*------------------ USERNAME ADD ------------------*/
$("#usernameAdd").click(function() { //on add input button click
    console.log("usercount: " + userCount);
    if (userCount < 4 && !indiv) {
        userCount = userCount + 1;
        $("#inputGroup").append('<h3>, </h3>');
        $("#inputGroup").append('<input type="text" class="usernameInput" placeholder="username" autocomplete="off" style="border-bottom: 1px solid #162252" onkeypress="this.style.width = ((this.value.length + 1) * 10) + \'px\';">'); //add input box
    }
    if (userCount == 4) {
        $("#usernameAdd").css("display", "none");
    } else {
        $("#usernameAdd").css("display", "inline-block");
    }
});


/*------------------ SLIDE OR LIST ------------------*/
$("[name='list-or-slide']").bootstrapSwitch();
$(".bootstrap-switch-label").html("<div></div>");
$(".bootstrap-switch").css("background","#162252");

$("input[name='list-or-slide']").on("switchChange.bootstrapSwitch", function(event, state) {
    if (state) {    // if switched to list
        topSugList();
    } else {        // if switched to slide
        topSugSlide();
    }
    $("#topsugslist").slideToggle(500);
    $("#topsugsslide").slideToggle(500);
});


/*------------------ LIKE OR RETWEET ------------------*/
$("[name='like-or-retweet']").bootstrapSwitch();
$(".bootstrap-switch-label").html("<div></div>");
$(".bootstrap-switch").css("background","#162252");

$("input[name='like-or-retweet']").on("switchChange.bootstrapSwitch", function(event, state) {
    if (state) {    // if switched to retweets
        RTnotLike = true;
        indivuser = indivuserretweets;
    } else {        // if switched to likes
        RTnotLike = false;
        indivuser = indivuserlikes;
    }
});


/*------------------ INDIV OR COMPARE ------------------*/
$("[name='indiv-or-compare']").bootstrapSwitch();
$(".bootstrap-switch-label").html("<div></div>");
$(".bootstrap-switch").css("background","#162252");

$("input[name='indiv-or-compare']").on("switchChange.bootstrapSwitch", function(event, state) {
    if (state) {    // if switched to individual
        indiv = true;
        console.log("only1");
        $("#inputGroup").html('<input type="text" class="usernameInput" placeholder="username" autocomplete="off" readonly="true"  onkeypress="this.style.width = ((this.value.length + 1) * 10) + \'px\';">');
        displayedSugs = indivuser;
        $(".fa").click();
    } else {        // if switched to compare
        indiv = false;
        displayedSugs = compareusers;
        $(".fa").click();
    }
});


/*------------------ SUGGESTION TYPE ------------------*/
//$("input[name='trendtype']").click(function(){
//    $(this).prop("checked", true);
//    if ($(this).val() === "indivuser") {
//        displayedSugs = indivuser;
//    } else if ($(this).val() === "compareusers") {
//        console.log("compareusers");
//        displayedSugs = compareusers;
//    }
//    topSugList();
//    topSugSlide();
//});