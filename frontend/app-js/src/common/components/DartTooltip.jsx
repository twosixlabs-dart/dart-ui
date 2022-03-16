import React from 'react';
import PropTypes from 'prop-types';

import Tooltip from '@material-ui/core/Tooltip';
import withStyles from '@material-ui/core/styles/withStyles';
import Typography from '@material-ui/core/Typography';
import HelpOutlineOutlinedIcon from '@material-ui/icons/HelpOutlineOutlined';

const HtmlTooltip = withStyles((theme) => ({
  tooltip: {
    backgroundColor: '#f5f5f9',
    color: 'rgba(0, 0, 0, 0.87)',
    maxWidth: 300,
    fontSize: theme.typography.pxToRem(12),
    border: '1px solid #dadde9',
  },
}))(Tooltip);

const DartTooltip = (props) => {
  const { body, children } = props;
  return (
    <HtmlTooltip
      interactive
      title={(
        <>
          <Typography color="inherit">{body}</Typography>
        </>
      )}
    >
      {children}
    </HtmlTooltip>
  );
};

DartTooltip.propTypes = {
  body: PropTypes.string.isRequired,
  children: PropTypes.node,
};

DartTooltip.defaultProps = {
  children: <HelpOutlineOutlinedIcon fontSize="small" color="primary" />,
};

export default DartTooltip;
