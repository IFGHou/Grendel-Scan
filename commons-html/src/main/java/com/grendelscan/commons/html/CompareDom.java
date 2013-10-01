package com.grendelscan.commons.html;




// TODO UCdetector: Remove unused code: 
// /**
//  * Compares the DOM structure of two nodes (which could be
//  * documents also). Does not detect duplicate nodes. This
//  * can be used to find subtle changes between different
//  * requests. There are many possible uses. For example,
//  * finding the session ids in URLs that change between login
//  * requests. Finding different text results between queries
//  * can help with SQL Injection testing.
//  * 
//  * 
//  * @author David Byrne
//  */
// public class CompareDom
// {
// 	Node masterOriginalNode, masterCandidateNode;
// 
// 	// Contains all of the nodes by node name, then by node
// 	// hash to prevent dups
// 	HashMap<String, HashMap<String, Node>> originalNodes, candidateNodes;
// 
// 	/**
// 	 * When compairing multiple nodes to an original, use
// 	 * {@link setCandidateNode(Node candidateDocument) setCandidateNode}
// 	 * to compare each candidate node. This will save time
// 	 * because the original node will not need to be
// 	 * re-parsed.
// 	 * 
// 	 * @param originalNode
// 	 *            The node against which comparisons are
// 	 *            made.
// 	 * @param candidateNode
// 	 *            The node that will be compared against
// 	 *            originalNode.
// 	 * 
// 	 */
// 	public CompareDom(Node originalNode, Node candiateNode)
// 	{
// 		masterOriginalNode = originalNode;
// 		masterCandidateNode = candiateNode;
// 		originalNodes = splitDom(originalNode);
// 		originalNodes = splitDom(masterCandidateNode);
// 	}
// 
// 	/**
// 	 * Returns a HashSet of nodes from the candidate node
// 	 * that appear similar to specified node. Currently,
// 	 * only the node and attributes are compared. Child
// 	 * nodes are not analyzed.
// 	 */
// 	public Set<Node> findSimilarInCandidate(Node node, int maxMatches)
// 	{
// 		return findSimilarInTarget(node, candidateNodes, maxMatches);
// 	}
// 
// 	/**
// 	 * Returns a HashSet of nodes from the original node
// 	 * that appear similar to specified node. Currently,
// 	 * only the node and attributes are compared. Child
// 	 * nodes are not analyzed.
// 	 * 
// 	 * @param node
// 	 *            The node to compare against in finding
// 	 *            matches
// 	 * @param maxMatches
// 	 *            Limits the returned values to the top
// 	 *            "maxMatches" results.
// 	 */
// 	public Set<Node> findSimilarInOriginal(Node node, int maxMatches)
// 	{
// 		return findSimilarInTarget(node, originalNodes, maxMatches);
// 	}
// 
// 	public Node getCandidateNode()
// 	{
// 		return masterCandidateNode;
// 	}
// 
// 	public Node getOriginalNode()
// 	{
// 		return masterOriginalNode;
// 	}
// 
// 	/**
// 	 * Returns a HashSet of all nodes that are in the
// 	 * candidate node, but not the original node.
// 	 * 
// 	 * Supports Node.DOCUMENT_TYPE_NODE, Node.ELEMENT_NODE,
// 	 * Node.TEXT_NODE, and Node.COMMENT_NODE
// 	 * 
// 	 */
// 	public HashSet<Node> inCandidateNotOriginal()
// 	{
// 		return compare(originalNodes, candidateNodes);
// 	}
// 
// 	/**
// 	 * Returns a HashSet of nodes matching the specified
// 	 * node name that are in the candidate node, but not the
// 	 * original node.
// 	 * 
// 	 * Supports Node.DOCUMENT_TYPE_NODE, Node.ELEMENT_NODE,
// 	 * Node.TEXT_NODE, Node.COMMENT_NODE, and any tag name
// 	 * 
// 	 * @param nodeName
// 	 *            The name of the node type to search for.
// 	 *            For a complete list, see the w3c
// 	 *            documentation for Node
// 	 */
// 	public HashSet<Node> inCandidateNotOriginal(String nodeName)
// 	{
// 		return compareNodeCategory(originalNodes, candidateNodes, nodeName);
// 	}
// 
// 	/**
// 	 * Returns a HashSet of all nodes that are in the
// 	 * original node, but not the candidate node.
// 	 * 
// 	 */
// 	public HashSet<Node> inOriginalNotCandidate()
// 	{
// 		return compare(candidateNodes, originalNodes);
// 	}
// 
// 	/**
// 	 * Returns a HashSet of nodes matching the specified
// 	 * node name that are in the original node, but not the
// 	 * candidate node.
// 	 * 
// 	 * Supports Node.DOCUMENT_TYPE_NODE, Node.ELEMENT_NODE,
// 	 * Node.TEXT_NODE, and Node.COMMENT_NODE
// 	 * 
// 	 * @param nodeName
// 	 *            The name of the node type to search for.
// 	 *            For a complete list, see the w3c
// 	 *            documentation for Node
// 	 * 
// 	 */
// 	public HashSet<Node> inOriginalNotCandidate(String nodeName)
// 	{
// 		return compareNodeCategory(candidateNodes, originalNodes, nodeName);
// 	}
// 
// 	public void setCandidateNode(Node candidateNode)
// 	{
// 		masterCandidateNode = candidateNode;
// 	}
// 
// 	private void addNode(Node target, HashMap<String, HashMap<String, Node>> nodes)
// 	{
// 		String nodeName = target.getNodeName();
// 		String hash = HtmlNodeUtilities.getNodeHash(target, false);
// 		HashMap<String, Node> nodeCategory;
// 
// 		if (nodes.containsKey(nodeName))
// 		{
// 			nodeCategory = nodes.get(nodeName);
// 			if (!nodeCategory.containsKey(hash))
// 			{
// 				nodeCategory.put(hash, target);
// 			}
// 		}
// 		else
// 		{
// 			nodeCategory = new HashMap<String, Node>();
// 			nodeCategory.put(hash, target);
// 			nodes.put(nodeName, nodeCategory);
// 		}
// 	}
// 
// 	/**
// 	 * Returns a set of all nodes that are in targetB, but
// 	 * not in targetA
// 	 */
// 	private HashSet<Node> compare(HashMap<String, HashMap<String, Node>> targetA, HashMap<String, HashMap<String, Node>> targetB)
// 	{
// 		HashSet<Node> nodes = new HashSet<Node>();
// 
// 		for (String nodeName: targetB.keySet())
// 		{
// 			nodes.addAll(compareNodeCategory(targetA, targetB, nodeName));
// 		}
// 		return nodes;
// 	}
// 
// 	private HashSet<Node> compareNodeCategory(HashMap<String, HashMap<String, Node>> targetA, HashMap<String, HashMap<String, Node>> targetB, String nodeName)
// 	{
// 		HashSet<Node> nodes = new HashSet<Node>();
// 		HashMap<String, Node> nodeCategoryA, nodeCategoryB;
// 		nodeCategoryB = targetB.get(nodeName);
// 		if (targetA.containsKey(nodeName))
// 		{
// 			nodeCategoryA = targetA.get(nodeName);
// 			for (String node: nodeCategoryB.keySet())
// 			{
// 				if (!nodeCategoryA.containsKey(node))
// 				{
// 					nodes.add(nodeCategoryB.get(node));
// 				}
// 			}
// 		}
// 		else
// 		{
// 			for (String node: nodeCategoryB.keySet())
// 			{
// 				nodes.add(nodeCategoryB.get(node));
// 			}
// 		}
// 		return nodes;
// 	}
// 
// 	private Set<Node> findSimilarInTarget(Node originalNode, HashMap<String, HashMap<String, Node>> targetNodes, int maxMatches)
// 	{
// 		int lowestGoodScore = 0;
// 		int nodeScore;
// 		HashMap<Node, Integer> resultNodes = new HashMap<Node, Integer>();
// 		HashMap<String, Node> candidates = targetNodes.get(originalNode.getNodeName());
// 
// 		for (Node candidateNode: candidates.values())
// 		{
// 			nodeScore = scoreNode(originalNode, candidateNode);
// 			if (resultNodes.size() < maxMatches)
// 			{
// 				resultNodes.put(candidateNode, nodeScore);
// 			}
// 			else if (nodeScore > lowestGoodScore)
// 			{
// 				for (Node node: resultNodes.keySet())
// 				{
// 					if (resultNodes.get(node) < nodeScore)
// 					{
// 						resultNodes.remove(node);
// 						resultNodes.put(candidateNode, nodeScore);
// 						lowestGoodScore = nodeScore;
// 						break;
// 					}
// 				}
// 			}
// 		}
// 
// 		return resultNodes.keySet();
// 	}
// 
// 	private int[] scoreAttribute(Attr originalAttribute, Attr candidateAttribute)
// 	{
// 		int score[];
// 
// 		String name = originalAttribute.getName().toLowerCase();
// 
// 		if (name.equals("href") || name.equals("src"))
// 		{
// 			score = scoreUriAttribute(originalAttribute, candidateAttribute);
// 		}
// 		else if (name.startsWith("on"))
// 		{
// 			score = scoreEventAttribute(originalAttribute, candidateAttribute);
// 		}
// 		else
// 		{
// 			score = scoreSimpleAttribute(originalAttribute, candidateAttribute);
// 		}
// 
// 		return score;
// 	}
// 
// 	private int scoreElement(Node original, Node candidate)
// 	{
// 		int score = 0;
// 		int maxScore = 0;
// 		int returnedScore[] = new int[2];
// 
// 		NamedNodeMap originalAttributes = original.getAttributes();
// 		NamedNodeMap candidateAttributes = candidate.getAttributes();
// 
// 		for (int index = 0; index < originalAttributes.getLength(); index++)
// 		{
// 			// Three points for a matching attribute value,
// 			// one point for a matching name only
// 			maxScore += 4;
// 			Attr originalAttribute = (Attr) originalAttributes.item(index);
// 			Attr candidateAttribute = (Attr) candidateAttributes.getNamedItem(originalAttribute.getName());
// 			if (candidateAttribute != null)
// 			{
// 				returnedScore = scoreAttribute(originalAttribute, candidateAttribute);
// 				score += returnedScore[0];
// 				maxScore += returnedScore[1];
// 			}
// 		}
// 
// 		return score;
// 	}
// 
// 	/**
// 	 * Same scoring technique as scoreSimpleAttribute, but
// 	 * with up to 10 points for a perfect match
// 	 */
// 	private int[] scoreEventAttribute(Attr originalAttribute, Attr candidateAttribute)
// 	{
// 		int finalScore[] = new int[2];
// 
// 		finalScore = scoreSimpleAttribute(originalAttribute, candidateAttribute);
// 		finalScore[1] = 10;
// 
// 		return finalScore;
// 	}
// 
// 	/**
// 	 * Currently, this will only compare the node and
// 	 * attributes. Child nodes are not analyzed.
// 	 */
// 	private int scoreNode(Node original, Node candidate)
// 	{
// 		int score = 0;
// 
// 		switch (original.getNodeType())
// 		{
// 
// 			case Node.ELEMENT_NODE:
// 			{
// 				score = scoreElement(original, candidate);
// 				break;
// 			}
// 		}
// 		return score;
// 	}
// 
// 	/**
// 	 * Up to 5 points for a perfect match
// 	 */
// 	private int[] scoreSimpleAttribute(Attr originalAttribute, Attr candidateAttribute)
// 	{
// 		int finalScore[] = new int[2];
// 		int score = 0;
// 		int maxLength;
// 		int levenshteinDistance;
// 		String originalValue;
// 		String candidateValue;
// 		float scaleIncrement;
// 
// 		if ((originalAttribute == null) || (candidateAttribute == null))
// 		{
// 			score = 0;
// 		}
// 		else
// 		{
// 			originalValue = originalAttribute.getValue();
// 			candidateValue = candidateAttribute.getValue();
// 
// 			if (originalValue.equals(candidateValue))
// 			{
// 				score = 5;
// 			}
// 			else if (originalValue.equalsIgnoreCase(candidateValue))
// 			{
// 				score = 4;
// 			}
// 			else
// 			{
// 				if (originalValue.length() > candidateValue.length())
// 				{
// 					maxLength = originalValue.length();
// 				}
// 				else
// 				{
// 					maxLength = candidateValue.length();
// 				}
// 
// 				score = StringUtils.scoreStringDifferenceIgnoreCase(originalValue, candidateValue, 4);
// 				/*
// 				levenshteinDistance = StringUtilities.getLevenshteinDistance(originalValue, candidateValue);
// 				scaleIncrement = (maxLength - 1) / 6;
// 				if (levenshteinDistance < scaleIncrement + 1)
// 				{
// 					score = 3;
// 				}
// 				else if (levenshteinDistance < 3 * scaleIncrement + 1)
// 				{
// 					score = 2;
// 				}
// 				else if (levenshteinDistance < 5 * scaleIncrement + 1)
// 				{
// 					score = 1;
// 				}
// 				else
// 				{
// 					score = 0;
// 				}
// 				*/
// 			}
// 		}
// 		finalScore[0] = score;
// 		finalScore[1] = 5;
// 
// 		return finalScore;
// 	}
// 
// 	/**
// 	 * 
// 	 * two points for protocol, five points for host name,
// 	 * two points for port, five points for path, 1 point
// 	 * for parameter name, 2 points for parameter value
// 	 * 
// 	 * This won't work well for URIs that use the path for
// 	 * query parameters
// 	 * 
// 	 * If one of the attributes isn't a URI, score it as a
// 	 * simple attribute
// 	 * 
// 	 * Currently, this doesn't compare relative & absolute
// 	 * URIs well, even if they are pointing to the same
// 	 * server. It would require the URI of the node to do
// 	 * this right. Oh well.
// 	 * 
// 	 */
// 	private int[] scoreUriAttribute(Attr originalAttribute, Attr candidateAttribute)
// 	{
// 		int finalScore[] = new int[2];
// 		int score = 0, maxScore = 0;
// 		URI originalURI, candidateURI;
// 
// 		try
// 		{
// 			originalURI = UriFactory.makeUri(originalAttribute.getValue(), false);
// 			candidateURI = UriFactory.makeUri(candidateAttribute.getValue(), false);
// 		}
// 		catch (URISyntaxException e) 
// 		{
// 			return scoreSimpleAttribute(originalAttribute, candidateAttribute);
// 		}
// 
// 		maxScore += 9;
// 
// 		if (! originalURI.isAbsolute() && ! candidateURI.isAbsolute())
// 		{
// 			score += 9;
// 		}
// 
// 		if (originalURI.isAbsolute() && candidateURI.isAbsolute())
// 		{
// 			if (originalURI.getScheme().equals(candidateURI.getScheme()))
// 			{
// 				score += 2;
// 			}
// 
// 			if (originalURI.getPort() == candidateURI.getPort())
// 			{
// 				score += 2;
// 			}
// 
// 			if (originalURI.getHost().equals(candidateURI.getHost()))
// 			{
// 				score += 5;
// 			}
// 
// 			if (originalURI.getPath().equals(candidateURI.getPath()))
// 			{
// 				score += 5;
// 			}
// 
// 			UriQuery originalQuery = new UriQuery(originalURI.getQuery());
// 			UriQuery candidateQuery = new UriQuery(candidateURI.getQuery());
// 
// 			for (String name: originalQuery.getParameters().keySet())
// 			{
// 				maxScore += 3;
// 				if (candidateQuery.containsParameter(name))
// 				{
// 					score++;
// 					if (originalQuery.getParameter(name).equals(candidateQuery.getParameter(name)))
// 					{
// 						score += 2;
// 					}
// 				}
// 			}
// 		}
// 
// 		finalScore[0] = score;
// 		finalScore[1] = maxScore;
// 
// 		return finalScore;
// 	}
// 
// 	private HashMap<String, HashMap<String, Node>> splitDom(Node target)
// 	{
// 		HashMap<String, HashMap<String, Node>> nodes = new HashMap<String, HashMap<String, Node>>(8);
// 		target.normalize();
// 		if (target instanceof Document)
// 		{
// 			HTMLDocumentImpl doc = (HTMLDocumentImpl) target;
// 			doc.normalizeDocument();
// 		}
// 
// 		splitNode(target, nodes);
// 
// 		return nodes;
// 	}
// 
// 	private void splitNode(Node target, HashMap<String, HashMap<String, Node>> nodes)
// 	{
// 		if (target.hasChildNodes())
// 		{
// 			NodeList children = target.getChildNodes();
// 			for (int index = 0; index < children.getLength(); index++)
// 			{
// 				splitNode(children.item(index), nodes);
// 			}
// 		}
// 		switch (target.getNodeType())
// 		{
// 			case Node.DOCUMENT_TYPE_NODE:
// 				;
// 			case Node.ELEMENT_NODE:
// 				;
// 			case Node.TEXT_NODE:
// 				;
// 			case Node.COMMENT_NODE:
// 				addNode(target, nodes);
// 		}
// 	}
// }
