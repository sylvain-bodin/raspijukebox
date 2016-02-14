/**
 *  Created by Akuma on 08/02/2016.
 */
'use strict';
module.exports = function (grunt) {
    // Load grunt tasks automatically
    require('load-grunt-tasks')(grunt);

    // Configurable paths for the application
    var appConfig = {
        app: require('./bower.json').appPath || 'app',
        dist: 'dist'
    };

    // Project configuration.
    grunt.initConfig({
        config: appConfig,
        // Metadata.
        pkg: grunt.file.readJSON('package.json'),
        // Watches files for changes and runs tasks based on the changed files
        watch: {
            bower: {
                files: ['bower.json'],
                tasks: ['wiredep']
            },
            js: {
                files: ['<%= config.app %>/src/{,*/}*.js'],
                tasks: ['newer:jshint:all'],
                options: {
                    livereload: '<%= connect.options.livereload %>'
                }
            },
            gruntfile: {
                files: ['Gruntfile.js']
            },
            styles: {
                files: ['<%= config.app %>/styles/{,*/}*.css'],
                tasks: ['newer:copy:styles']
            },
            livereload: {
                options: {
                    livereload: '<%= connect.options.livereload %>'
                },
                files: [
                    '<%= config.app %>/{,*/}*.html',
                    '.tmp/styles/{,*/}*.css',
                    '<%= config.app %>/img/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
                ]
            }
        },
        // The actual grunt server settings
        connect: {
            options: {
                port: 9000,
                // Change this to '0.0.0.0' to access the server from outside.
                hostname: 'localhost',
                livereload: 35729
            },
            livereload: {
                options: {
                    open: true,
                    base: [
                        '<%= config.app %>'
                    ],
                    middleware: function (connect, options) {
                        var middlewares = [];
                        //middlewares.push(modRewrite(['!\\.html|\\.js|\\.svg|\\.css|\\.png$ /index.html [L]'])); //Matches everything that does not contain a '.' (period)
                        options.base.forEach(function (base) {
                            middlewares.push(connect.static(base));
                        });

                        middlewares.push(
                            connect.static('.tmp'),
                            connect().use(
                                '/bower_components',
                                connect.static('./app/bower_components')
                            )
                        );
                        return middlewares;
                    }
                }
            },
            dev: {
                options: {
                    open: true,
                    base: [
                        '<%= config.app %>'
                    ],
                    livereload: false
                }
            },
            dist: {
                options: {
                    open: true,
                    base: '<%= config.dist %>'
                }
            }
        },
        // Empties folders to start fresh
        clean: {
            dist: {
                files: [{
                    dot: true,
                    src: [
                        '.tmp',
                        '<%= config.dist %>/{,*/}*',
                        '!<%= config.dist %>/.git*'
                    ]
                }]
            },
            server: '.tmp'
        },

        // Reads HTML for usemin blocks to enable smart builds that automatically
        // concat, minify and revision files. Creates configurations in memory so
        // additional tasks can operate on them
        useminPrepare: {
            html: '<%= config.app %>/index.html',
            options: {
                dest: '<%= config.dist %>',
                flow: {
                    html: {
                        steps: {
                            js: ['concat', 'uglifyjs'],
                            css: ['cssmin']
                        },
                        post: {}
                    }
                }
            }
        },
        // Performs rewrites based on filerev and the useminPrepare configuration
        usemin: {
            html: ['<%= config.dist %>/{,*/}*.html'],
            css: ['<%= config.dist %>/styles/{,*/}*.css'],
            options: {
                assetsDirs: ['<%= config.dist %>', '<%= config.dist %>/img']
            }
        },
        imagemin: {
            dist: {
                files: [{
                    expand: true,
                    cwd: '<%= config.app %>/img',
                    src: '{,*/}*.{png,jpg,jpeg,gif}',
                    dest: '<%= config.dist %>/img'
                }]
            }
        },

        svgmin: {
            dist: {
                files: [{
                    expand: true,
                    cwd: '<%= config.app %>/img',
                    src: '{,*/}*.svg',
                    dest: '<%= config.dist %>/img'
                }]
            }
        },

        htmlmin: {
            dist: {
                options: {
                    collapseWhitespace: true,
                    conservativeCollapse: true,
                    collapseBooleanAttributes: true,
                    removeCommentsFromCDATA: true,
                    removeOptionalTags: true
                },
                files: [{
                    expand: true,
                    cwd: '<%= config.dist %>',
                    src: ['*.html', 'views/{,*/}*.html'],
                    dest: '<%= config.dist %>'
                }]
            }
        },

        // ng-annotate tries to make the code safe for minification automatically
        // by using the Angular long form for dependency injection.
        ngAnnotate: {
            dist: {
                files: [{
                    expand: true,
                    cwd: '.tmp/concat/scripts',
                    src: ['*.js', '!oldieshim.js'],
                    dest: '.tmp/concat/scripts'
                }]
            }
        },

        // Copies remaining files to places other tasks can use
        copy: {
            dist: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: '<%= config.app %>',
                    dest: '<%= config.dist %>',
                    src: [
                        '*.{ico,png,txt}',
                        '.htaccess',
                        '*.html',
                        'views/{,*/}*.html',
                        'img/{,*/}*.{webp}',
                        'fonts/*'
                    ]
                }, {
                    expand: true,
                    cwd: '.tmp/images',
                    dest: '<%= config.dist %>/img',
                    src: ['generated/*']
                }]
            },
            styles: {
                expand: true,
                cwd: '<%= config.app %>/styles',
                dest: '.tmp/styles/',
                src: '{,*/}*.css'
            }
        },
        // Automatically inject Bower components into the app
        wiredep: {
            app: {
                src: ['<%= config.app %>/index.html'],
                ignorePath: /\.\.\//
            }
        },
        // Automatically inject js/css components (except bowers') into the app
        includeSource: {
            options: {
                basePath: 'app'
            },
            dev: {
                files: {
                    'app/index.html': 'app/index.html'
                }
            }
        }
    });

    // Default task.
    grunt.registerTask('serve', 'Compile then start a connect web server', function (target) {
        if (target === 'dist') {
            return grunt.task.run(['build', 'connect:dist:keepalive']);
        }

        grunt.task.run([
            'clean:server',
            'wiredep',
            'includeSource:dev',
            'copy:styles',
            'connect:dev:keepalive',
            'watch'
        ]);
    });

    grunt.registerTask('build', [
        'clean:dist',
        'wiredep',
        'includeSource:dev',
        'useminPrepare',
        'copy:styles',
        'ngAnnotate',
        'copy:dist',
        'imagemin',
        'svgmin',
        'cssmin',
        'uglify',
        'usemin',
        'htmlmin'
    ]);
    grunt.registerTask('default', [
        'build'
    ]);
};