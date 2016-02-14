/**
 *  Created by Akuma on 09/02/2016.
 */
angular.module('raspiJukeBox.api', ['ngResource']).factory('Jukebox', function (config, $http, $resource) {
    'use strict';
    return {
        play: function () {
            return $resource(config.endpoint + '/play?songId=:id');
        }
        ,
        list: function () {
            return $resource(config.endpoint + '/jsonList')
        },
        stop: function () {
            return $resource(config.endpoint + '/stop')
        },
        playAll: function () {
            return $resource(config.endpoint + '/playAll')
        }
    };
});