'use strict';

// Declare app level module which depends on views, and components
angular.module('raspiJukeBox', [
    'ngRoute',
    'raspiJukeBox.main'
]).config(['$routeProvider', function ($routeProvider) {
    $routeProvider.otherwise({redirectTo: '/main'});
}]);
