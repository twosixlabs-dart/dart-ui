import React from 'react';
import PropTypes from 'prop-types';

import Paper from '@material-ui/core/Paper';
import makeStyles from '@material-ui/core/styles/makeStyles';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';

const useStyles = makeStyles(() => ({
  paper: {
    height: 48,
  },
  smallPaper: {
    padding: 3,
  },
  container: {
    height: '100%',
  },
}));

const makeTitle = (title) => (
  <Typography variant="subtitle1" component="h3" color="primary">
    <b>{title}</b>
  </Typography>
);

export default function Header(props) {
  const { children, title, small } = props;
  const classes = useStyles();

  const paperClass = small ? classes.smallPaper : classes.paper;

  return (
    <Paper square className={paperClass}>
      <Grid container direction="column" justifyContent="center" alignItems="center" className={classes.container}>
        <Grid container direction="row" justifyContent="center" alignItems="center" className={classes.container}>
          {title ? makeTitle(title) : children}
        </Grid>
      </Grid>
    </Paper>
  );
}

Header.propTypes = {
  children: PropTypes.node,
  title: PropTypes.string,
  small: PropTypes.bool,
};

Header.defaultProps = {
  small: false,
  title: '',
  children: '',
};
