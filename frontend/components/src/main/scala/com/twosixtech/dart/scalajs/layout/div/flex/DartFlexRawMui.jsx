import React, { Component } from 'react';
// import ReactDOM from 'react-dom';
import { Box, withStyles } from '@material-ui/core';

import DartFlexProps from './DartFlexProps';

const styles = () => ({
  root: {
    height: '100%',
  },
  container: {},
  items: {},
});

function alignItems(str) {
  switch (str) {
    case 'start': {
      return 'flex-start';
    }

    case 'center': {
      return 'center';
    }

    case 'end': {
      return 'flex-end';
    }

    default:
      return 'flex-start';
  }
}

class DartFlex extends Component {
  render() {
    const {
      direction,
      align,
      items,
      classes,
    } = this.props;

    return (
      <Box
        display="flex"
        flexDirection={direction}
        alignItems={alignItems(align)}
        className={classes.container}
      >
        {items.map((item, i) => {
          const {
            element,
            flexGrow,
            flexShrink,
            flexBasis,
            key,
          } = item;
          const itemAlign = item.align;
          const itemClasses = item.classes;

          return (
            <Box
              flexGrow={flexGrow}
              flexShrink={flexShrink}
              flexBasis={flexBasis}
              alignSelf={itemAlign ? alignItems(itemAlign) : null}
              key={key || `grid-item-${i}`}
              className={`${classes.items} ${itemClasses.root}`}
            >
              {element}
            </Box>
          );
        })}
      </Box>
    );
  }
}

DartFlex.propTypes = DartFlexProps.propTypes;
DartFlex.defaultProps = DartFlexProps.defaultProps;

export default withStyles(styles)(DartFlex);
