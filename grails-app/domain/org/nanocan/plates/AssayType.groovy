package org.nanocan.plates

class AssayType implements Serializable{

    String name
    String type
    int wavelength

    static searchable = true

    static constraints = {
        name
        type inList: ['Fluorescence', 'Absorbance', 'Luminescence', 'Other']

    }

    String toString(){
        name
    }

}
