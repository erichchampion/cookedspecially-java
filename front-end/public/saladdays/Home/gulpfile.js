var gulp = require('gulp');
var preprocess = require('gulp-preprocess');
var minifyCss = require('gulp-minify-css');
var imagemin = require('gulp-imagemin');
var uncss = require('gulp-uncss');
var del = require('del');
var minifyHTML = require('gulp-minify-html');
var uglify = require('gulp-uglify');
var shell = require('gulp-shell')

gulp.task('upload', ['build'], shell.task([
  'rsync --partial --progress --rsh=ssh -r /Users/amitchowdhary/pp/saladdays-web/deploy/*  saladdays@128.199.216.41:/home/saladdays/website'
]));

gulp.task('html', ['clean'], function() {
  var opts = {
    conditionals: true,
    spare:true
  };

  return gulp.src('./Home/*.html')
    .pipe(minifyHTML(opts))
    .pipe(gulp.dest('./deploy/'));
});

gulp.task('html-scripts', function() {
  gulp.src('./deploy/*.html')
    .pipe(uglify())
    .pipe(gulp.dest('./deploy/'));
});

gulp.task('scripts', ['clean'], function() {
  gulp.src('./Home/js/*.js')
    .pipe(uglify())
    .pipe(gulp.dest('./deploy/js/'));
});

gulp.task('css', ['clean'], function() {
  return gulp.src('./Home/css/*.css')
    .pipe(minifyCss({compatibility: 'ie8'}))
    .pipe(gulp.dest('./deploy/css/'));
});

gulp.task('copy-images', ['clean'], function() {
  return gulp.src('./Home/images/**')
    .pipe(gulp.dest('./deploy/images/'));
});

gulp.task('copy-fonts', ['clean'], function() {
  return gulp.src('./Home/fonts/*')
    .pipe(gulp.dest('./deploy/fonts/'));
});

gulp.task('copy-res', ['clean'], function() {
  return gulp.src('./Home/resources/*')
    .pipe(gulp.dest('./deploy/resources/'));
});

gulp.task('copy-sm', ['clean'], function() {
  return gulp.src('./Home/swiftmailer/**')
    .pipe(gulp.dest('./deploy/swiftmailer/'));
});

gulp.task('images', ['clean'], function() {
  return gulp.src('Home/images/**')
    // Pass in options to the task
    .pipe(imagemin({optimizationLevel: 5}))
    .pipe(gulp.dest('deploy/images/'));
});

gulp.task('php', ['clean'], function() {
  return gulp.src('Home/*.php')
    // Pass in options to the task
    .pipe(imagemin({optimizationLevel: 5}))
    .pipe(gulp.dest('deploy/'));
});

gulp.task('clean', function(cb) {
  // You can use multiple globbing patterns as you would with `gulp.src`
  return del(['deploy/*'], cb);
});



gulp.task('build', ['clean', 'html', 'php', 'scripts', 'css', 'copy-res', 'copy-images', 'copy-fonts', 'copy-sm']);

gulp.task('default', ['build', 'upload']);
//'images']);