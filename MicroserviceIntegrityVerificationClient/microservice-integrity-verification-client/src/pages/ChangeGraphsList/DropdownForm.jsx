import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Chip,
  Collapse,
  FormControl,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
  TextField
} from '@mui/material';
import { ExpandMore, ExpandLess } from '@mui/icons-material';
import * as api from '../../api/MicroserviceVerificationAPI';

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

export default function DropdownForm({refreshCallback}) {
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [newOption, setNewOption] = useState('');
  const [open, setOpen] = useState(false);
  const [options, setOptions] = useState([]);

  useEffect(() => {
    api.getMicroservices()
    .then(data =>
        setOptions(data.map(dto => dto.name)));
  }, []);

  const handleChange = (event) => {
    const {
      target: { value },
    } = event;
    setSelectedOptions(typeof value === 'string' ? value.split(',') : value);
  };

  const handleAddOption = () => {
    if (newOption && !selectedOptions.includes(newOption)) {
      setSelectedOptions([...selectedOptions, newOption]);
      setNewOption('');
    }
  };

  const handleCreate = () => {
    console.log('Selected options:', selectedOptions);
    api.createChangeGraph({associatedMicroservices: selectedOptions});
    refreshCallback();
    // Здесь можно добавить логику для обработки выбранных опций
    // alert(`Создано с опциями: ${selectedOptions.join(', ')}`);
  };

  return (
    <Box sx={{ width: '100%', maxWidth: 800, m: 2}}>
      <Button
        sx={{width: '100%'}}
        variant="contained"
        onClick={() => setOpen(!open)}
        endIcon={open ? <ExpandLess /> : <ExpandMore />}
      >
        Создать новый граф изменений
      </Button>

      <Collapse in={open}>
        <Box sx={{ mt: 2, p: 2, border: '1px solid #ccc', borderRadius: 1 }}>
          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel id="demo-multiple-chip-label">Микросервисы</InputLabel>
            <Select
              labelId="demo-multiple-chip-label"
              id="demo-multiple-chip"
              multiple
              value={selectedOptions}
              onChange={handleChange}
              input={<OutlinedInput id="select-multiple-chip" label="Микросервисы" />}
              renderValue={(selected) => (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                  {selected.map((value) => (
                    <Chip key={value} label={value} />
                  ))}
                </Box>
              )}
              MenuProps={MenuProps}
            >
              {options.map((option) => (
                <MenuItem key={option} value={option}>
                  {option}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
            <TextField
              fullWidth
              label="Добавить новый микросервис"
              value={newOption}
              onChange={(e) => setNewOption(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleAddOption()}
            />
            <Button variant="contained" onClick={handleAddOption}>
              Добавить
            </Button>
          </Box>

          <Button
            variant="contained"
            color="primary"
            onClick={handleCreate}
            fullWidth
          >
            Создать
          </Button>
        </Box>
      </Collapse>
    </Box>
  );
}
