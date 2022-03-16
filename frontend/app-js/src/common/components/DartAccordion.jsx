import React from 'react';
import PropTypes from 'prop-types';
import Typography from '@material-ui/core/Typography';
import AccordionSummary from '@material-ui/core/AccordionSummary';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import AccordionDetails from '@material-ui/core/AccordionDetails';
import Accordion from '@material-ui/core/Accordion';

export default function DartAccordion(props) {
  const {
    item,
    id,
    title,
    children,
    expanded,
    onChange,
    onTransitioned,
    timeout,
    classes,
    className,
  } = props;

  if (item) {
    return (
      <Accordion
        expanded={false}
        classes={classes}
      >
        <AccordionSummary
          expandIcon=""
        >
          {title ? <Typography variant="subtitle1"><b>{title}</b></Typography> : children}
        </AccordionSummary>
        <AccordionDetails />
      </Accordion>
    );
  }

  return (
    <Accordion
      onChange={onChange}
      expanded={expanded}
      classes={classes}
      TransitionProps={{
        onEntered: onTransitioned,
        onExited: onTransitioned,
        timeout,
      }}
      className={className}
    >
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls={`panel${id}-content`}
        id={`panel${id}-header`}
      >
        <Typography variant="subtitle1"><b>{title}</b></Typography>
      </AccordionSummary>
      <AccordionDetails>
        {children}
      </AccordionDetails>
    </Accordion>
  );
}

DartAccordion.propTypes = {
  item: PropTypes.bool,
  id: PropTypes.string.isRequired,
  title: PropTypes.node.isRequired,
  children: PropTypes.node.isRequired,
  classes: PropTypes.objectOf(PropTypes.string),
  expanded: PropTypes.bool,
  onChange: PropTypes.func,
  onTransitioned: PropTypes.func,
  timeout: PropTypes.number,
  className: PropTypes.string,
};

DartAccordion.defaultProps = {
  classes: {},
  expanded: undefined,
  timeout: undefined,
  onChange: undefined,
  item: false,
  onTransitioned: () => {},
  className: 'dart-accordion',
};
