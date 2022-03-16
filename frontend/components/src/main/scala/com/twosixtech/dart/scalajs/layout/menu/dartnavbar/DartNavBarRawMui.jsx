import React, { Component } from 'react';
import {
  withStyles,
  Toolbar,
  Typography,
  AppBar,
  IconButton,
} from '@material-ui/core';
import MenuIcon from '@material-ui/icons/Menu';
// import PropTypes from 'prop-types';
import DartNavBarProps from './DartNavBarProps';

const styles = (theme) => ({
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
  },
});

class DartNavBar extends Component {
  render() {
    const {
      menuOpened,
      openMenu,
      closeMenu,
      classes,
    } = this.props;

    const handleMenuButtonClick = () => {
      if (menuOpened) closeMenu();
      else openMenu();
    };

    return (
      <div>
        <AppBar position="fixed" className={classes.appBar} color="primary">
          <Toolbar variant="dense">
            <IconButton
              edge="start"
              aria-label="menu"
              aria-controls="simple-menu"
              aria-haspopup="true"
              onClick={handleMenuButtonClick}
              className="dart-ui-navbar-hamburger-button"
              color="inherit"
            >
              <MenuIcon />
            </IconButton>
            <Typography variant="h6">
              DART
            </Typography>
          </Toolbar>
        </AppBar>
      </div>
    );
  }
}

DartNavBar.propTypes = DartNavBarProps.propTypes;

DartNavBar.defaultProps = DartNavBarProps.defaultProps;

export default withStyles(styles)(DartNavBar);
