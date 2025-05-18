import React, { Fragment, useEffect, useRef, useState } from 'react';
import cytoscape from 'cytoscape';
import { createPopper } from '@popperjs/core';
import './Graph.css';

const Graph = ({ graphData }) => {
    const graphRef = useRef(null);
    const tooltipRef = useRef(null);
    const [cy, setCy] = useState(null);
    const [popperInstance, setPopperInstance] = useState(null);
    const [currentEdge, setCurrentEdge] = useState(null);

    useEffect(() => {
        console.log(graphData)
        if (graphData) {
            drawGraph(graphRef, setCy, graphData);
        }
    }, [graphData]);

    useEffect(() => {
        if (!cy || !tooltipRef.current) return;

        const updatePopperPosition = (edge) => {
            if (!edge || !popperInstance) return;
            
            const midPoint = edge.midpoint();
            const screenPosition = cy.container().getBoundingClientRect();
            const zoom = cy.zoom();
            const pan = cy.pan();
            
            const screenX = (midPoint.x * zoom + pan.x) + screenPosition.left;
            const screenY = (midPoint.y * zoom + pan.y) + screenPosition.top;

            popperInstance.state.elements.reference = {
                getBoundingClientRect: () => ({
                    width: 0,
                    height: 0,
                    top: screenY,
                    right: screenX,
                    bottom: screenY,
                    left: screenX,
                }),
            };
            popperInstance.update();
        };

        const handleMouseOver = (event) => {
            const edge = event.target;
            if (!edge.data('message')) return;

            setCurrentEdge(edge);
            tooltipRef.current.innerHTML = edge.data('message');
            tooltipRef.current.style.display = 'block';

            if (popperInstance) {
                popperInstance.destroy();
            }

            const midPoint = edge.midpoint();
            const screenPosition = cy.container().getBoundingClientRect();
            const zoom = cy.zoom();
            const pan = cy.pan();
            
            const screenX = (midPoint.x * zoom + pan.x) + screenPosition.left;
            const screenY = (midPoint.y * zoom + pan.y) + screenPosition.top;

            const popper = createPopper(
                {
                    getBoundingClientRect: () => ({
                        width: 0,
                        height: 0,
                        top: screenY,
                        right: screenX,
                        bottom: screenY,
                        left: screenX,
                    }),
                },
                tooltipRef.current,
                {
                    placement: 'top',
                    modifiers: [
                        {
                            name: 'offset',
                            options: {
                                offset: [0, 8],
                            },
                        },
                    ],
                }
            );

            setPopperInstance(popper);
        };

        const handleMouseOut = () => {
            tooltipRef.current.style.display = 'none';
            if (popperInstance) {
                popperInstance.destroy();
                setPopperInstance(null);
            }
            setCurrentEdge(null);
        };

        const handleDrag = () => {
            if (currentEdge) {
                updatePopperPosition(currentEdge);
            }
        };

        const handlePanZoom = () => {
            if (currentEdge) {
                updatePopperPosition(currentEdge);
            }
        };

        cy.on('mouseover', 'edge', handleMouseOver);
        cy.on('mouseout', 'edge', handleMouseOut);
        cy.on('drag', 'node', handleDrag);
        cy.on('pan zoom', handlePanZoom);
        cy.on('mouseover', 'edge', (e) => updatePopperPosition(e.target));

        return () => {
            cy.off('mouseover', 'edge', handleMouseOver);
            cy.off('mouseout', 'edge', handleMouseOut);
            cy.off('drag', 'node', handleDrag);
            cy.off('pan zoom', handlePanZoom);
            cy.off('mouseover', 'edge', (e) => updatePopperPosition(e.target));
            if (popperInstance) {
                popperInstance.destroy();
            }
        };
    });

    return (
        <Fragment>
            <div ref={graphRef} style={{ width: '100%', height: '100%', backgroundColor: '#A1A1A1' }}></div>
            <div ref={tooltipRef} className="tooltip" style={{ display: 'none' }} />
        </Fragment>
    );
};

const drawGraph = (graphRef, setCy, data) => {
    if (data) {
        const cy = cytoscape({
            container: graphRef.current,
            elements: getElements(data),
            style: getStyle(),
            layout: {
                name: 'random',
                spacingFactor: 1.1,
                fit: true,
                avoidOverlap: true,
            }
        });
        setCy(cy);
    }
};

const getElements = (data) => {
    const nodesElements = data.nodes.map(node => ({
        data: {
            id: node.id,
            status: node.status.toLowerCase()
        }
    }));

    const edgeElements = data.edges.map(edge => ({
        data: {
            id: edge.id,
            source: edge.source,
            target: edge.target,
            status: edge.status.toLowerCase(),
            message: edge.message
        }
    }));

    return [...nodesElements, ...edgeElements];
};

const getStyle = () => {
    return [
        {
            selector: 'node',
            style: {
                'label': 'data(id)',
                'border-style': 'solid',
                'border-width': '3px'
            }
        },
        {
            selector: 'node[status="ok"]',
            style: {
                'backgroundColor': '#6A91F4',
                'border-color': '#C0C3FE'
            }
        },
        {
            selector: 'node[status="error"]',
            style: {
                'backgroundColor': '#e36868',
                'border-color': '#9F2020'
            }
        },
        {
            selector: 'node[status="warning"]',
            style: {
                'backgroundColor': '#c5b607',
                'border-color': '#ec5918'
            }
        },
        {
            selector: 'edge',
            style: {
                'width': '3px',
                'target-arrow-shape': 'triangle',
                'arrow-scale': 1.5,
                'curve-style': 'bezier',
            }
        },
        {
            selector: 'edge[status = "ok"]',
            style: {
                'line-color': '#209F2F',
                'target-arrow-color': '#209F2F'
            }
        },
        {
            selector: 'edge[status = "error"]',
            style: {
                'line-color': '#9F2020',
                'target-arrow-color': '#9F2020'
            }
        }
    ];
};

export default Graph;