'use strict';

angular.module('raspiJukeBox.version.version-directive', [])

    .directive('appVersion', ['version', function (version) {
        return function (scope, elm) {
            elm.text(version);
        };
    }]);
