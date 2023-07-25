import React from 'react';
import { Text, type ViewStyle } from 'react-native';

export const PrismaPlaceholder = (props: PrismaProps) => {
    // Retorno con el banner envuelto en un modal, o sin modal
    return <Text {...props}>Platform not supported</Text>;
};

interface PrismaProps {
    placeholderName: string;
    style: ViewStyle;
}
