/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.graph;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.internal.graph.CompoundBreakCycles;
import org.eclipse.draw2d.internal.graph.CompoundHorizontalPlacement;
import org.eclipse.draw2d.internal.graph.CompoundPopulateRanks;
import org.eclipse.draw2d.internal.graph.CompoundRankSorter;
import org.eclipse.draw2d.internal.graph.CompoundVerticalPlacement;
import org.eclipse.draw2d.internal.graph.ConvertCompoundGraph;
import org.eclipse.draw2d.internal.graph.GraphVisitor;
import org.eclipse.draw2d.internal.graph.InitialRankSolver;
import org.eclipse.draw2d.internal.graph.MinCross;
import org.eclipse.draw2d.internal.graph.PlaceEndpoints;
import org.eclipse.draw2d.internal.graph.RankAssigmentSolver;
import org.eclipse.draw2d.internal.graph.SortSubgraphs;
import org.eclipse.draw2d.internal.graph.TightSpanningTreeSolver;

/**
 * Performs a graph layout on a <code>CompoundDirectedGraph</code>.  The input format is
 * the same as for {@link DirectedGraphLayout}.  All nodes, including subgraphs and their
 * children, should be added to the {@link DirectedGraph#nodes} field.
 * <P>
 * The requirements for this algorithm are the same as those of
 * <code>DirectedGraphLayout</code>, with the following exceptions:
 * <UL>
 *    <LI>There is an implied edge between a subgraph and each of its member nodes. These
 *    edges form the containment graph <EM>T</EM>. Thus, the compound directed graph
 *    <EM>CG</EM> is said to be connected iff Union(<EM>G</EM>, <EM>T</EM>) is connected,
 *    where G represents the given nodes (including subgraphs) and edges.
 *    
 *    <LI>This algorithm will remove any compound cycles found in the input graph
 *    <em>G</em> by inverting edges according to a heuristic until no more cycles are
 *    found. A compound cycle is defined as: a cycle comprised of edges from <EM>G</EM>,
 *    <EM>T</EM>, and <em>T<SUP>-1</SUP></em>, in the form
 *    (c<SUP>*</SUP>e<SUP>+</SUP>p<SUP>*</SUP>e<SUP>+</SUP>)*, where       
 *    <em>T<SUP>-1</SUP></em> is the backwards graph of <EM>T</EM>, c element of T, e  
 *    element of G, and p element of T<SUP>-1</SUP>.
 * </UL>
 * 
 * @author Randy Hudson
 * @since 2.1.2
 */
public final class CompoundDirectedGraphLayout extends GraphVisitor {

/**
 * @since 3.1
 */
public CompoundDirectedGraphLayout() {
	steps.add(new CompoundBreakCycles());
	steps.add(new ConvertCompoundGraph());
	steps.add(new InitialRankSolver());
	steps.add(new TightSpanningTreeSolver());
	steps.add(new RankAssigmentSolver());
	steps.add(new CompoundPopulateRanks());
	steps.add(new CompoundVerticalPlacement());
	steps.add(new MinCross(new CompoundRankSorter()));
	steps.add(new SortSubgraphs());
	steps.add(new CompoundHorizontalPlacement());
	steps.add(new PlaceEndpoints());
}

List steps = new ArrayList();

/**
 * Lays out the given compound directed graph.
 * @param graph the graph to layout
 */
public void visit(DirectedGraph graph) {
	for (int i = 0; i < steps.size(); i++) {
		GraphVisitor visitor = (GraphVisitor)steps.get(i);
		visitor.visit(graph);
	}
	for (int i = steps.size() - 1; i >= 0; i--) {
		GraphVisitor visitor = (GraphVisitor)steps.get(i);
		visitor.revisit(graph);
	}
}

}
