'use strict';

angular.module('raspiJukeBox.version', [
        'raspiJukeBox.version.version-directive'
    ])

    .value('version', function () {
        return '0.1';
    });
