import {CatalogService} from "Frontend/generated/endpoints";
import {useEffect, useState} from "react";
import {Grid, GridColumn} from "@vaadin/react-components";

export default function ComponentsView() {
    const [inputs, setInputs] = useState<Array<String>>();
    const [selected, setSelected] = useState<String | null | undefined>();

    useEffect(() => {
        // @ts-ignore
        CatalogService.inputs().then(setInputs);
    }, [])

    return (
        <div className="p-m flex gap-m">
            <Grid
                items={inputs}
                onActiveItemChanged={e => setSelected(e.detail.value)}
                selectedItems={[selected]}>

                <GridColumn/>
                <GridColumn path="lastName"/>
                <GridColumn path="email" autoWidth/>
                <GridColumn path="company.name" header="Company name"/>
            </Grid>
        </div>
    )
}