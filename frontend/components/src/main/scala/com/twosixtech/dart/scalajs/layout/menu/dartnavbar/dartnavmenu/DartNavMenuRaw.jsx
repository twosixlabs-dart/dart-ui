import React, { Component } from 'react';
import { withStyles, Drawer, List } from '@material-ui/core';

import PropTypes from 'prop-types';

import DartNavMenuProps from './DartNavMenuProps';
// eslint-disable-next-line import/extensions
import Text from '../../../text/TextRawMui';

const styles = (theme) => ({
  menu: {
    padding: 35,
    paddingTop: 73,
  },
  selected: {
    color: theme.palette.primary.main,
    marginBottom: 12,
  },
  notSelected: {
    marginBottom: 12,
  }
});

const refCb = (fn) => (ele) => {
  if (fn && ele) {
    fn(ele.parentElement);
  }
};

class DartNavMenuRaw extends Component {
  render() {
    const {
      menuOpened,
      closeMenu,
      menuItems,
      refFn,
      classes,
    } = this.props;

    const menuLinkElement = (key, text, onClick, isSelected) => {
      const textClasses = { root: isSelected ? classes.selected : classes.notSelected };
      return (
        <div key={key}>
          <Text
            size="large"
            onClick={() => { onClick(); closeMenu(); }}
            clickable
            classes={textClasses}
          >
            {isSelected ? <b>{text}</b> : text}
          </Text>
        </div>
      );
    };

    const menuLinks = menuItems
      .map((
        {
          key,
          text,
          onClick,
          isSelected,
        },
      ) => menuLinkElement(key, text, onClick, isSelected));

    return (
      <Drawer
        variant="persistent"
        anchor="left"
        open={menuOpened}
      >
        <div
          className={`dart-ui-menu ${classes.menu}`}
          ref={refCb(refFn)}
        >
          <List>
            {menuLinks}
          </List>
        </div>
      </Drawer>
    );
  }
}

DartNavMenuRaw.propTypes = {
  ...DartNavMenuProps.propTypes,
  classes: PropTypes.shape({
    menu: PropTypes.string.isRequired,
    selected: PropTypes.string.isRequired,
    notSelected: PropTypes.string.isRequired,
  }).isRequired,
};

DartNavMenuRaw.defaultProps = DartNavMenuProps.defaultProps;

export default withStyles(styles)(DartNavMenuRaw);
