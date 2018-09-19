var gameModule = angular.module('gameModule', []);

var rows = [
                [
                    {'id': '0', 'playerToken': '', 'class': 'box', 'player': ''},
                    {'id': '1', 'playerToken': '', 'class': 'box', 'player': ''},
                    {'id': '2', 'playerToken': '', 'class': 'box', 'player': ''},
                    {'id': '3', 'playerToken': '', 'class': 'box', 'player': ''}
                ],
                [
                    {'id': '11', 'playerToken': '', 'class': 'box', 'player': ''},
                    {'id': '100', 'playerToken': '0', 'class': 'middlecard', 'player': ''},
                    {'id': '101', 'playerToken': '1', 'class': 'middlecard', 'player': ''},
                    {'id': '4', 'playerToken': '', 'class': 'box', 'player': ''}
                ],
                [
                    {'id': '10', 'playerToken': '', 'class': 'box', 'player': ''},
                    {'id': '102', 'playerToken': '2', 'class': 'middlecard', 'player': ''},
                    {'id': '103', 'playerToken': '3', 'class': 'middlecard', 'player': ''},
                    {'id': '5', 'playerToken': '', 'class': 'box', 'player': ''}
                ],
                [
                    {'id': '9', 'playerToken': '', 'class': 'box', 'player': ''},
                    {'id': '8', 'playerToken': '', 'class': 'box', 'player': ''},
                    {'id': '7', 'playerToken': '', 'class': 'box', 'player': ''},
                    {'id': '6', 'playerToken': '', 'class': 'box', 'player': ''}
                ]
            ];

var playerBoxes = [
                                        rows[0][0],
                                        rows[0][1],
                                        rows[0][2],
                                        rows[0][3],
                                        rows[1][3],
                                        rows[2][3],
                                        rows[3][3],
                                        rows[3][2],
                                        rows[3][1],
                                        rows[3][0],
                                        rows[2][0],
                                        rows[1][0]
                    ];

var hardCodedPlayerMap = {
                            "zji":"123e4567-e89b-12d3-a456-426655440000",
                            "dboy":"123e4567-e89b-12d3-a456-426655440001",
                            "renzhi":"123e4567-e89b-12d3-a456-426655440002",
                            "wtf":"123e4567-e89b-12d3-a456-426655440003",
                            "juicy":"123e4567-e89b-12d3-a456-426655440004",
                            "rex":"123e4567-e89b-12d3-a456-426655440005",
                            "fboy":"123e4567-e89b-12d3-a456-426655440006",
                            "sola":"123e4567-e89b-12d3-a456-426655440007",
                            "":"123e4567-e89b-12d3-a456-426655440008",
                            "":"123e4567-e89b-12d3-a456-426655440009",
                            "":"123e4567-e89b-12d3-a456-426655440010",
                            "":"123e4567-e89b-12d3-a456-426655440011"
                         };

var hardCodedReversedPlayerMap = {
                            "123e4567-e89b-12d3-a456-426655440000":"zji",
                            "123e4567-e89b-12d3-a456-426655440001":"dboy",
                            "123e4567-e89b-12d3-a456-426655440002":"renzhi",
                            "123e4567-e89b-12d3-a456-426655440003":"wtf",
                            "123e4567-e89b-12d3-a456-426655440004":"juicy",
                            "123e4567-e89b-12d3-a456-426655440005":"rex",
                            "123e4567-e89b-12d3-a456-426655440006":"fboy",
                            "123e4567-e89b-12d3-a456-426655440007":"sola",
                            "123e4567-e89b-12d3-a456-426655440008":"",
                            "123e4567-e89b-12d3-a456-426655440009":"",
                            "123e4567-e89b-12d3-a456-426655440010":"",
                            "123e4567-e89b-12d3-a456-426655440011":""
                         };

var roomID = "321e4567-e89b-12d3-a456-426655440222";

var playerToken = null;

var username = null;

gameModule.controller('newGameController', ['$rootScope', '$scope', '$http', '$location',

    function (rootScope, scope, http, location) {

        rootScope.gameId = null;
        scope.userCredential = null;

        scope.enterVillage = function () {
            var data = scope.userCredential;
            playerToken = hardCodedPlayerMap[data.username];

            if(playerToken != null) {
                username = scope.userCredential.username;
                location.path('/game/' + roomID);
            }

        }

    }
]);


gameModule.controller('gamesToJoinController', ['$scope', '$http', '$location',
    function (scope, http, location) {

        scope.gamesToJoin = [];

    }]);


gameModule.controller('playerGamesController', ['$scope', '$http', '$location', '$routeParams',
    function (scope, http, location, routeParams) {

        scope.playerGames = [];

    }]);


gameModule.controller('gameController', ['$rootScope', '$routeParams', '$scope', '$http',
    function (rootScope, routeParams, scope, http) {

        scope.rows = rows;
        scope.initialRole = "TBD";
        scope.gameID = null;
        scope.playerActionResponse = "Please wait for the action phase.";
        scope.actionTurn = false;
        scope.votePhase = false;
        scope.seerFormedData = null;
        scope.troubleMakerFormedData = null;
        scope.playerUserName = username;
        scope.voteResponse = "Please wait for the vote phase.";
        scope.finalResult = "Please wait...";
        scope.receiveMessage = "Please wait for the host to start a new game."
        scope.isWinner = "?";
        scope.yourCurrentRole = "?";

        if(playerToken != null) {
            var ws = new WebSocket('ws://172.93.35.237:15674/ws');
            var client = Stomp.over(ws);
            ws.onclose = function() {
                console.log('close web socket');
                client.disconnect();
            };

            client.connect("magic", "F1reflies", onConnect, onError, "vhost");

            function onConnect() {

                var id = client.subscribe("/queue/test-" + playerToken, function(d) {

                    if(d.body == "wakeup") {
                        scope.actionTurn = true;
                        scope.receiveMessage = d.body;
                    } else if(d.body == "openeyes") {
                        scope.receiveMessage = d.body;
                    } else if(d.body == "vote") {
                        scope.receiveMessage = d.body;
                        scope.votePhase = true;
                    } else if(d.body == "timeout") {
                        scope.receiveMessage = d.body;
                        if(scope.actionTurn) {
                            scope.playerActionResponse = d.body;
                            scope.actionTurn = false;
                        }
                    } else if(d.body.startsWith("Phase")) {
                        scope.receiveMessage = d.body;
                    } else if(d.body.startsWith("resolve")) {
                        scope.voteResponse = "time out."
                        scope.receiveMessage = d.body;
                        scope.votePhase = false;
                        var formedData = {};
                        formedData.gameID = scope.gameID;
                        formedData.roomID = roomID;
                        formedData.userToken = playerToken;
                        formedData.frontEndSendTime = Date.now;
                        var params = JSON.stringify(formedData);
                        http.post("/resolve", params, {
                            headers: {
                                'Content-Type': 'application/json;charset=UTF-8'
                            }
                        }).success(function (data, status, headers, config) {
                            scope.finalResult = data;
                            if(data.winners.indexOf(playerToken) > -1) {
                                scope.isWinner = "Win";
                            } else {
                                scope.isWinner = "Loss";
                            }
                            scope.receiveMessage = "Please wait for the host to start a new game."
                            for (var i = 0; i < data.reveal.length; i++) {
                                var playerInfo = data.reveal[i];
                                if(playerToken == playerInfo.playerToken) {
                                     scope.yourCurrentRole = playerInfo.currentRole;
                                }
                            }
                        }).error(function (data, status, headers, config) {
                            console.log("error");
                        });
                    } else {
                        var jsonBody = JSON.parse(d.body);

                        if(jsonBody["gameID"] != null) {
                            scope.gameID = jsonBody["gameID"];
                            scope.receiveMessage = "Start a new Game.";
                            scope.initialRole = "TBD";
                            scope.playerActionResponse = "Please wait for the action phase.";
                            scope.finalResult = "Please wait...";
                            scope.voteResponse = "Please wait for the vote phase."
                            scope.isWinner = "?"
                            scope.yourCurrentRole = "?";
                            initialRows();
                        } else if(jsonBody["role"] != null) {
                            scope.initialRole = jsonBody["role"];
                            var playerTokenList = jsonBody["playerTokenList"];
                            for (var i = 0; i < playerTokenList.length; i++) {
                                playerBoxes[i].playerToken = playerTokenList[i];
                                playerBoxes[i].player = hardCodedReversedPlayerMap[playerTokenList[i]];
                                scope.rows = rows;
                            }
                        }
                    }
                    scope.$apply();
                });
            }

            function onError(e) {
              console.log("STOMP ERROR", e);
            }
        } else {
            console.log(playerToken);
        }

        var gameStatus;
        getInitialData()

        function getInitialData() {

        }

        function getMoveHistory() {

        }

        function initialRows() {
            scope.rows = [
                    [
                        {'id': '0', 'playerToken': '', 'class': 'box', 'player': ''},
                        {'id': '1', 'playerToken': '', 'class': 'box', 'player': ''},
                        {'id': '2', 'playerToken': '', 'class': 'box', 'player': ''},
                        {'id': '3', 'playerToken': '', 'class': 'box', 'player': ''}
                    ],
                    [
                        {'id': '11', 'playerToken': '', 'class': 'box', 'player': ''},
                        {'id': '100', 'playerToken': '0', 'class': 'middlecard', 'player': ''},
                        {'id': '101', 'playerToken': '1', 'class': 'middlecard', 'player': ''},
                        {'id': '4', 'playerToken': '', 'class': 'box', 'player': ''}
                    ],
                    [
                        {'id': '10', 'playerToken': '', 'class': 'box', 'player': ''},
                        {'id': '102', 'playerToken': '2', 'class': 'middlecard', 'player': ''},
                        {'id': '103', 'playerToken': '3', 'class': 'middlecard', 'player': ''},
                        {'id': '5', 'playerToken': '', 'class': 'box', 'player': ''}
                    ],
                    [
                        {'id': '9', 'playerToken': '', 'class': 'box', 'player': ''},
                        {'id': '8', 'playerToken': '', 'class': 'box', 'player': ''},
                        {'id': '7', 'playerToken': '', 'class': 'box', 'player': ''},
                        {'id': '6', 'playerToken': '', 'class': 'box', 'player': ''}
                    ]
                ];
        }

        scope.markPlayerMove = function (column) {
            if(column.playerToken == "3") {
                return;
            }

            if(scope.actionTurn) {
                switch(scope.initialRole) {
                    case "WEREWOLF":
                        if(column.playerToken == "0" || column.playerToken == "1" || column.playerToken == "2" || column.playerToken == "3") {
                            var formedData = {};
                            formedData.gameID = scope.gameID;
                            formedData.roomID = roomID;
                            formedData.userToken = playerToken;
                            formedData.rawData = {};
                            formedData.rawData.index = Number(column.playerToken);
                            formedData.frontEndSendTime = Date.now;
                            var params = JSON.stringify(formedData);
                            http.post("/action/werewolf", params, {
                                headers: {
                                    'Content-Type': 'application/json;charset=UTF-8'
                                }
                            }).success(function (data, status, headers, config) {
                                scope.playerActionResponse = data;
                                scope.actionTurn = false;
                            }).error(function (data, status, headers, config) {
                                console.log("error");
                            });
                        }
                        break;
                    case "SEER":
                        if(scope.seerFormedData == null) {
                            scope.seerFormedData = {};
                            if(column.playerToken == "0" || column.playerToken == "1" || column.playerToken == "2" || column.playerToken == "3") {
                                scope.seerFormedData.gameID = scope.gameID;
                                scope.seerFormedData.roomID = roomID;
                                scope.seerFormedData.userToken = playerToken;
                                scope.seerFormedData.rawData = {};
                                scope.seerFormedData.rawData.index = [];
                                scope.seerFormedData.rawData.index.push(Number(column.playerToken));
                            } else {
                                if(playerToken != column.playerToken) {
                                    scope.seerFormedData.gameID = scope.gameID;
                                    scope.seerFormedData.roomID = roomID;
                                    scope.seerFormedData.userToken = playerToken;
                                    scope.seerFormedData.rawData = {};
                                    scope.seerFormedData.rawData.playerID = column.playerToken;
                                    var params = JSON.stringify(scope.seerFormedData);
                                    scope.actionTurn = false;
                                    http.post("/action/seer", params, {
                                        headers: {
                                            'Content-Type': 'application/json;charset=UTF-8'
                                        }
                                    }).success(function (data, status, headers, config) {
                                        scope.playerActionResponse = data;
                                    }).error(function (data, status, headers, config) {
                                        console.log("error");
                                    });

                                    scope.seerFormedData = null;
                                }
                            }
                        } else {
                            if(column.playerToken == "0" || column.playerToken == "1" || column.playerToken == "2" || column.playerToken == "3") {
                                if(scope.seerFormedData.rawData.index[0] != column.playerToken) {
                                    scope.seerFormedData.rawData.index.push(Number(column.playerToken));
                                    scope.seerFormedData.frontEndSendTime = Date.now;
                                    var params = JSON.stringify(scope.seerFormedData);
                                    scope.actionTurn = false;
                                    http.post("/action/seer", params, {
                                        headers: {
                                            'Content-Type': 'application/json;charset=UTF-8'
                                        }
                                    }).success(function (data, status, headers, config) {
                                        scope.playerActionResponse = data;
                                    }).error(function (data, status, headers, config) {
                                        console.log("error");
                                    });
                                    scope.seerFormedData = null;
                                }
                            }
                        }
                        break;
                    case "ROBBER":
                        if(!(column.playerToken == "0" || column.playerToken == "1" || column.playerToken == "2" || column.playerToken == "3") && column.playerToken != playerToken) {
                            var formedData = {};
                            formedData.gameID = scope.gameID;
                            formedData.roomID = roomID;
                            formedData.userToken = playerToken;
                            formedData.rawData = {};
                            formedData.rawData.robbed = column.playerToken;
                            formedData.frontEndSendTime = Date.now;
                            var params = JSON.stringify(formedData);
                            http.post("/action/robber", params, {
                                headers: {
                                    'Content-Type': 'application/json;charset=UTF-8'
                                }
                            }).success(function (data, status, headers, config) {
                                scope.playerActionResponse = data;
                                scope.actionTurn = false;
                            }).error(function (data, status, headers, config) {
                                console.log("error");
                            });
                        }
                        break;
                    case "TROUBLEMAKER":
                        if(!(column.playerToken == "0" || column.playerToken == "1" || column.playerToken == "2" || column.playerToken == "3") && column.playerToken != playerToken) {
                            if(scope.troubleMakerFormedData == null) {
                                scope.troubleMakerFormedData = {};
                                scope.troubleMakerFormedData.gameID = scope.gameID;
                                scope.troubleMakerFormedData.roomID = roomID;
                                scope.troubleMakerFormedData.userToken = playerToken;
                                scope.troubleMakerFormedData.rawData = {};
                                scope.troubleMakerFormedData.rawData.players = [];
                                scope.troubleMakerFormedData.rawData.players.push(column.playerToken);
                            } else {
                                if(playerToken != column.playerToken) {
                                    scope.troubleMakerFormedData.rawData.players.push(column.playerToken);
                                    scope.troubleMakerFormedData.frontEndSendTime = Date.now;
                                    var params = JSON.stringify(scope.troubleMakerFormedData);
                                    scope.actionTurn = false;
                                    http.post("/action/troublemaker", params, {
                                        headers: {
                                            'Content-Type': 'application/json;charset=UTF-8'
                                        }
                                    }).success(function (data, status, headers, config) {
                                        scope.playerActionResponse = data;
                                    }).error(function (data, status, headers, config) {
                                        console.log("error");
                                    });
                                    scope.troubleMakerFormedData = null;
                                }
                            }
                        }
                        break;
                    case "DRUNK":
                    if(column.playerToken == "0" || column.playerToken == "1" || column.playerToken == "2" || column.playerToken == "3") {
                        var formedData = {};
                        formedData.gameID = scope.gameID;
                        formedData.roomID = roomID;
                        formedData.userToken = playerToken;
                        formedData.rawData = {};
                        formedData.rawData.swap = Number(column.playerToken);
                        formedData.frontEndSendTime = Date.now;
                        var params = JSON.stringify(formedData);
                        http.post("/action/drunk", params, {
                            headers: {
                                'Content-Type': 'application/json;charset=UTF-8'
                            }
                        }).success(function (data, status, headers, config) {
                            scope.playerActionResponse = data;
                            scope.actionTurn = false;
                        }).error(function (data, status, headers, config) {
                            console.log("error");
                        });
                    }
                        break;
                    default:
                        break;
                }
            } else if(scope.votePhase) {
                if(!(column.playerToken == "0" || column.playerToken == "1" || column.playerToken == "2" || column.playerToken == "3")) {
                    var formedData = {};
                    formedData.gameID = scope.gameID;
                    formedData.roomID = roomID;
                    formedData.userToken = playerToken;
                    formedData.rawData = {};
                    formedData.rawData.voteTo = column.playerToken;
                    formedData.frontEndSendTime = Date.now;
                    var params = JSON.stringify(formedData);
                    http.post("/vote", params, {
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        }
                    }).success(function (data, status, headers, config) {
                        scope.voteResponse = data;
                        scope.votePhase = false;
                    }).error(function (data, status, headers, config) {
                        console.log("error");
                    });
                }
            } else {
                console.log("NOT IN YOUR ACTION PHASE!");
            }
        };
    }
]);



