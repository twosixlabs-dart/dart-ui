import React, { useRef } from 'react';
import PropTypes from 'prop-types';

import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';

import CdrTextSection from './CdrTextSection';
import CdrTextSectionSimple from './CdrTextSectionSimple';

const useStyles = makeStyles((theme) => ({
  paragraph: {
    height: theme.typography.fontSize,
  },
  headingWrapper: {
    paddingTop: 25,
  },
  byLine: {},
  section: {},
  chunkRoot: {
    maxWidth: '76ch',
    margin: 'auto',
    paddingLeft: 10,
    paddingRight: 10,
  },
}));

function CdrTextLineBreak() {
  const classes = useStyles();
  return <div className={`cdr-text-item-line-break ${classes.paragraph}`} />;
}

function CdrTextHeader(props) {
  const {
    title,
    author,
    date,
  } = props;

  const classes = useStyles();

  const titleElement = title === '' ? '' : (
    <Typography variant="h6" color="textPrimary">
      {title}
    </Typography>
  );

  const authorWithComma = date === '' ? author : `${author},`;
  const authorElement = author === '' ? '' : (
    <Grid item>
      <Typography variant="subtitle1" component="span" color="textPrimary" classes={{ root: classes.byLine }}>
        {authorWithComma.toUpperCase()}
      </Typography>
    </Grid>
  );

  const dateElement = date === '' ? '' : (
    <Typography variant="subtitle1" component="span" color="textPrimary" classes={{ root: classes.byLine }}>
      <em>{date}</em>
    </Typography>
  );

  return title === '' && author === '' ? <div /> : (
    <Grid
      container
      direction="column"
      alignItems="flex-start"
      className={`cdr-text-item-header ${classes.headingWrapper}`}
      spacing={1}
    >
      <Grid item xs={12}>
        {titleElement}
      </Grid>
      <Grid item xs={12}>
        <Grid container direction="row" alignItems="center" spacing={1}>
          {authorElement}
          {dateElement}
        </Grid>
      </Grid>
    </Grid>
  );
}

CdrTextHeader.propTypes = {
  title: PropTypes.string.isRequired,
  author: PropTypes.string.isRequired,
  date: PropTypes.string.isRequired,
};

function CdrTextItem(props) {
  const {
    chunk,
    index,
    style,
    textOnly,
  } = props;

  const itemRef = useRef(null);

  const classes = useStyles();

  // useEffect(() => {
  //   const { length } = chunk;
  //   const newHeight = itemRef.current.offsetHeight;
  //   if (!lenHtWdRatioRecomputed) {
  //     dispatch(addTextSizeSample(length, pageSize, newHeight));
  //   }
  // });

  let textElement;
  if (chunk.header) {
    textElement = (
      <CdrTextHeader
        title={chunk.title}
        author={chunk.author}
        date={chunk.date}
      />
    );
  } else if (chunk.paragraph) {
    textElement = (
      <CdrTextLineBreak />
    );
  } else if (textOnly) {
    textElement = (
      <CdrTextSectionSimple
        index={index}
        text={chunk.text}
      />
    );
  } else {
    textElement = (
      <CdrTextSection
        index={index}
        text={chunk.text}
        chunkOffset={chunk.offset}
      />
    );
  }

  return (
    <div style={style} id={index} className="cdr-text-item">
      <div ref={itemRef} className={classes.chunkRoot}>
        {textElement}
      </div>
    </div>
  );
}

CdrTextItem.propTypes = {
  chunk: PropTypes.shape({
    text: PropTypes.string,
    offset: PropTypes.number,
    length: PropTypes.number,
    paragraph: PropTypes.bool,
    header: PropTypes.bool,
    title: PropTypes.string,
    author: PropTypes.string,
    date: PropTypes.string,
  }).isRequired,
  index: PropTypes.number.isRequired,
  textOnly: PropTypes.bool,
  style: PropTypes.shape({}),
};

CdrTextItem.defaultProps = {
  textOnly: false,
  style: {},
};

export default CdrTextItem;
