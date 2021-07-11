var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var app = express();


// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

const auth = require('./middleware/auth');

// setup db connection
const mongoose = require('mongoose');
// 127.0.0.1:27017
// Change to hostname after following schema: username:pw@host:port/dbname
mongoose.connect('mongodb://localhost/tudas_db', { useNewUrlParser: true, useUnifiedTopology: true });
const db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function () {
  console.log("DB connected!")
  // we're connected!
});


// setup corresponding routes
var indexRouter = require('./routes/index');
var usersRouter = require('./routes/userIO');
var challengeRouter = require('./routes/challengeIO');
var authRouter = require('./routes/auth');
app.use('/', indexRouter);
app.use('/user', auth, usersRouter);
app.use('/challenge', auth, challengeRouter);
app.use('/auth', authRouter);


// catch 404 and forward to error handler
app.use(function (req, res, next) {
  next(createError(404));
});

// error handler
app.use(function (err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
