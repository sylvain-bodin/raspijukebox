/**
 *  Created by Akuma on 09/02/2016.
 */
angular.module('raspiJukeBox.main', ['raspiJukeBox.api'])
    .config(function ($routeProvider) {
        $routeProvider.when('/main', {
            templateUrl: 'src/views/main/main.html',
            controller: 'MainCtrl'

        });
    })
    .controller('MainCtrl', function ($scope, Jukebox) {
        'use strict';
        $scope.songs = [];
        Jukebox.list().query().$promise.then(function (songs) {
            angular.forEach(songs, function (value) {
                var song = {
                    id: value.id,
                    artist: value.artist,
                    title: value.title
                };
                this.push(song);
            }, $scope.songs);
        });
        $scope.play = function (id) {
            Jukebox.play().get({id: id}).$promise.then(function (results) {
                var text = "";
                angular.forEach(results, function (value) {
                    if (typeof value === 'string') {
                        text += value;
                    }
                });
                Materialize.toast(text, 2000, 'rounded');
            });
        };
        $scope.stop = function () {
            Jukebox.stop().get().$promise.then(function (results) {
                var text = "";
                angular.forEach(results, function (value) {
                    if (typeof value === 'string') {
                        text += value;
                    }
                });
                Materialize.toast(text, 2000, 'rounded');
            });
        };
        $scope.playAll = function () {
            Jukebox.playAll().get().$promise.then(function (results) {
                var text = "";
                angular.forEach(results, function (value) {
                    if (typeof value === 'string') {
                        if (value === '\n') {
                            value = '<br/>';
                        }
                        text += value;
                    }
                });
                Materialize.toast(text, 4000, 'rounded');
            });
        };
    });